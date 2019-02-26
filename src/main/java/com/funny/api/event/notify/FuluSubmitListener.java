package com.funny.api.event.notify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.config.FuluConfig;
import com.funny.utils.SignUtils;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

/**
 * 去福禄平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class FuluSubmitListener {

    private static final Logger logger = LoggerFactory.getLogger(FuluSubmitListener.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private OrderRequestRecordService orderRequestRecordService;

    @Autowired
    private WareInfoService wareInfoService;

    @Autowired
    private FuluConfig fuluConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(FuluSubmitEvent fuluSubmitEvent) throws UnknownHostException, JsonProcessingException {
        Integer id = Integer.parseInt(String.valueOf(fuluSubmitEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是待充值状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.WAIT_PROCESS) {
            return;
        }
        WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(orderFromYouzanEntity.getWareNo());
        if (wareInfoEntity == null) {
            //TODO 没有这个商品要退款
        }

        Map map = SignUtils.getFuluHeader("kamenwang.order.add");
        // 业务参数
        // 合作商家订单号（唯一不重复）
        map.put("customerorderno", orderFromYouzanEntity.getOrderNo());
        // 福禄商品编号
        map.put("productid", "1204405");
        // 用户编号
        map.put("customerid", "803683");
        // 购买数量
        map.put("buynum", "1");
        // 充值账号
        map.put("chargeaccount", "123456");
        // 提交订单的回调地址
        map.put("notifyurl", "https://www.lckj.shop/api/praise/order/notify");
        String param = SignUtils.MaptoString(map);
        // 将秘钥拼接到URL参数对后
        String postData = param + fuluConfig.getKey();
        String sign = MD5Utils.MD5(postData);
        // 将签名添加到URL参数后
        String request = fuluConfig.getUrl() + "?" + param + "&sign=" + sign.toUpperCase();
        // 发送请求并记录
        OrderRequestRecordEntity orderRequestRecordEntity = orderRequestRecordService.saveRequest(request,orderFromYouzanEntity.getId());
        ResponseEntity<String> responseEntity;
        Map result;
        try {
            orderFromYouzanEntity.setLastRechargeTime(new Date());
            responseEntity = restTemplate.postForEntity(request, null, String.class);
            orderRequestRecordEntity.setResponse(responseEntity.getBody());
            result = objectMapper.readValue(responseEntity.getBody(),Map.class);
        } catch (Exception e) {
            // 请求异常直接记录，然后就返回。等待定时器重试
            orderRequestRecordEntity.setException(e.getMessage());
            orderRequestRecordService.update(orderRequestRecordEntity);
            return;
        }

        // 福禄平台受理失败,等待退款，2407为下单超时，2115为添加订单失败。当前状态不明还不能直接判定为失败。等待主动查询或者通知。
        if (result.get("MessageCode") != null
                && (!"2407".equals(result.get("MessageCode").toString()))
                && (!"2115".equals(result.get("MessageCode").toString()))) {
            orderRequestRecordEntity.setException(responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderRequestRecordService.update(orderRequestRecordEntity);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }
        // 响应错误码为1206时是查询错误：系统出现异常，可持续下单处理，可查询时间超过十分钟以后，不能查到订单可做关单处理或重新下单处理)
        if(result.get("MessageCode") != null &&"1206".equals(result.get("MessageCode").toString())){
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.WAIT_PROCESS);
            orderRequestRecordService.update(orderRequestRecordEntity);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

        // 福禄平台已经受理订单，改变订单为受理中（等待通知或者在主动定时查询中处理）
        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
        orderRequestRecordService.update(orderRequestRecordEntity);
        orderFromYouzanService.update(orderFromYouzanEntity);
    }
}
