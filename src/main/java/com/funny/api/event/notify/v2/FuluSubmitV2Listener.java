package com.funny.api.event.notify.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.api.event.notify.YouzanRefundEvent;
import com.funny.config.FuluConfig;
import com.funny.utils.DateUtils;
import com.funny.utils.IPUtils;
import com.funny.utils.SignUtils;
import com.youzan.open.sdk.util.hash.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 去福禄平台下单监听器（20190428版本）
 *
 * @author liyanjun
 */
@Component
public class FuluSubmitV2Listener {

    private static final Logger logger = LoggerFactory.getLogger(FuluSubmitV2Listener.class);

    @Autowired
    protected FuluConfig fuluConfig;

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
    public void onApplicationEvent(FuluSubmitV2Event fuluSubmitV2Event) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(fuluSubmitV2Event.getSource()));
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
            return;
        }

        String url = fuluConfig.getKamiUrl();
        Map<String, String> map = getCommonParam();
        // 非卡密类型需要填写
        if (wareFuluInfoEntity.getType() == WareFuluInfoEntity.TYPE_NOT_CARD) {
            // 充值账号
            if (wareFuluInfoEntity.getMark() == null || "".equals(wareFuluInfoEntity.getMark())) {
                map.put("ChargeAccount", orderFromYouzanEntity.getRechargeInfo());
            } else {
                map.put("ChargeAccount", String.valueOf(objectMapper.readValue(orderFromYouzanEntity.getRechargeInfo(), Map.class).get(wareFuluInfoEntity.getMark())));
            }
            url = fuluConfig.getFeiKamiUrl();
        }
        // 业务参数
        // 合作商家订单号（唯一不重复）
        map.put("CustomerOrderNo", orderFromYouzanEntity.getOrderNo());
        // 福禄商品编号
        map.put("ProductId", String.valueOf(wareFuluInfoEntity.getProductId()));
//        map.put("BuyerIP", IPUtils.getRandomIp());
        // 计算购买数量，QQ的面值是1元，然后算出具体的面值。当面值超过5时，要走大额渠道
        Integer count = wareFuluInfoEntity.getNum() * orderFromYouzanEntity.getNum();
        // 购买数量
        map.put("BuyNum", String.valueOf(count));
        if (count > fuluConfig.getHuge()) {
            // 福禄商品编号(大额渠道)
            map.put("ProductId", String.valueOf(wareFuluInfoEntity.getProductHugeId()));
        }

        String param = SignUtils.MaptoString(map);
        // 将秘钥拼接到URL参数对后
        String postData = param + fuluConfig.getAppSecret();
        String sign = MD5Utils.MD5(postData);
        map.put("Sign", sign);

        // 发送请求并记录
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);

        OrderRequestRecordEntity orderRequestRecordEntity = orderRequestRecordService.saveRequest(url + "参数：" + objectMapper.writeValueAsString(map), orderFromYouzanEntity.getId());

        ResponseEntity<String> responseEntity;
        Map result;
        try {
            HttpEntity<Map<String, String>> request = new HttpEntity<>(map, headers);
            orderFromYouzanEntity.setLastRechargeTime(new Date());
            responseEntity = restTemplate.postForEntity(url, request, String.class);
            orderRequestRecordEntity.setResponse(responseEntity.getBody());
            result = objectMapper.readValue(responseEntity.getBody(), Map.class);
        } catch (Exception e) {
            // 请求异常直接记录，然后就返回。等待定时器重试
            orderRequestRecordEntity.setException(e.getMessage());
            orderRequestRecordService.update(orderRequestRecordEntity);
            return;
        }

        // 福禄平台受理失败,等待退款，但是当请求为3000外部订单号已存在时，等待主动查询或者通知。
        if (result.get("State") != null && !"Success".equals(result.get("State")) && !"3000".equals(result.get("Code").toString())) {
            orderRequestRecordEntity.setException(responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderRequestRecordService.update(orderRequestRecordEntity);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "福禄平台受理失败"));
            return;
        }

        // 福禄平台已经受理订单，改变订单为受理中（等待通知或者在主动定时查询中处理）
        if(orderFromYouzanEntity.getOrderPrice() == null || orderFromYouzanEntity.getOrderPrice().compareTo(BigDecimal.ZERO) <= 0) {
            Map m = (Map) result.get("Result");
            orderFromYouzanEntity.setOrderPrice(BigDecimal.valueOf(Double.parseDouble(m.get("OrderPrice").toString())).multiply(BigDecimal.valueOf(Double.parseDouble(m.get("BuyNum").toString()))));
        }
        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
        orderRequestRecordService.update(orderRequestRecordEntity);
        orderFromYouzanService.update(orderFromYouzanEntity);
    }

    private Map<String, String> getCommonParam() {
        Map map = new HashMap(8);
        map.put("AppKey", fuluConfig.getAppKey());
        map.put("TimeStamp", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        map.put("Format", "json");
        map.put("SignType", "md5");
        map.put("V", "1.0");
        return map;
    }

}
