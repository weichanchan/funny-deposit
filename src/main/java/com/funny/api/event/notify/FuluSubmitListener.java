package com.funny.api.event.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.config.FuluConfig;
import com.funny.utils.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 去福禄平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class FuluSubmitListener extends AbstractFuluListener {

    private static final Logger logger = LoggerFactory.getLogger(FuluSubmitListener.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private OrderRequestRecordService orderRequestRecordService;

    @Autowired
    private WareFuluInfoService wareFuluInfoService;

    @Autowired
    private ApplicationContext applicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(FuluSubmitEvent fuluSubmitEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(fuluSubmitEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是待充值状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.WAIT_PROCESS) {
            return;
        }
        WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(orderFromYouzanEntity.getWareNo());
        if (wareFuluInfoEntity == null) {
            orderFromYouzanEntity.setException("商品不可售，退款。");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "商品不可售"));
        }

        Map<String, String> map = getFuluHeader("kamenwang.order.add");
        // 业务参数
        // 合作商家订单号（唯一不重复）
        map.put("customerorderno", orderFromYouzanEntity.getOrderNo());
        // 福禄商品编号
        map.put("productid", String.valueOf(wareFuluInfoEntity.getProductId()));
        // 用户编号
        map.put("customerid", fuluConfig.getUserId());
        // 购买数量
        map.put("buynum", String.valueOf(wareFuluInfoEntity.getNum()));
        // 充值账号
        map.put("chargeaccount", String.valueOf(objectMapper.readValue(orderFromYouzanEntity.getRechargeInfo(), Map.class).get(wareFuluInfoEntity.getMark())));
        // 提交订单的回调地址
        map.put("notifyurl", fuluConfig.getNotifyUrl());
        // 发送请求并记录
        String request = getRequestString(map);
        OrderRequestRecordEntity orderRequestRecordEntity = orderRequestRecordService.saveRequest(request, orderFromYouzanEntity.getId());
        ResponseEntity<String> responseEntity;
        Map result;
        try {
            orderFromYouzanEntity.setLastRechargeTime(new Date());
            responseEntity = restTemplate.postForEntity(request, null, String.class);
            orderRequestRecordEntity.setResponse(responseEntity.getBody());
            result = objectMapper.readValue(responseEntity.getBody(), Map.class);
        } catch (Exception e) {
            // 请求异常直接记录，然后就返回。等待定时器重试
            orderRequestRecordEntity.setException(e.getMessage());
            orderRequestRecordService.update(orderRequestRecordEntity);
            return;
        }

        // 福禄平台受理失败,等待退款，但是当请求为2407为下单超时，2115为添加订单失败。当前状态不明还不能直接判定为失败。视为他们平台已经受理，等待主动查询或者通知。
        if (result.get("MessageCode") != null
                && (!"2407".equals(result.get("MessageCode").toString()))
                && (!"2115".equals(result.get("MessageCode").toString()))) {
            orderRequestRecordEntity.setException(responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
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
