package com.funny.api.event.notify.a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.api.event.notify.AbstractFuluListener;
import com.funny.api.event.notify.YouzanRefundEvent;
import com.funny.api.event.notify.superman.SupermanSubmitEvent;
import com.funny.config.AConfig;
import com.funny.config.FuluConfig;
import com.funny.config.SupermanConfig;
import com.funny.utils.SignUtils;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 去超人平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class SupermanSubmitListener {

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
    @Autowired
    protected SupermanConfig supermanConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(SupermanSubmitEvent supermanSubmitEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(supermanSubmitEvent.getSource()));
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
        MultiValueMap map = new LinkedMultiValueMap(16);
        map.put("user", supermanConfig.getUsername());
        // 合作商家订单号（唯一不重复）
        map.put("order", orderFromYouzanEntity.getOrderNo());
        map.put("code", "102");
        map.put("token", supermanConfig.getToken());
        map.put("pass", supermanConfig.getPassword());
        map.put("spid", wareFuluInfoEntity.getProductId());

        // 充值账号
        if (wareFuluInfoEntity.getMark() == null || "".equals(wareFuluInfoEntity.getMark())) {
            map.put("beizhu", objectMapper.writeValueAsString(Collections.singletonList(orderFromYouzanEntity.getRechargeInfo())));
        } else {
            map.put("beizhu", objectMapper.writeValueAsString(Collections.singletonList(String.valueOf(objectMapper.readValue(orderFromYouzanEntity.getRechargeInfo(), Map.class).get(wareFuluInfoEntity.getMark())))));
        }

        // 发送请求并记录
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        OrderRequestRecordEntity orderRequestRecordEntity = orderRequestRecordService.saveRequest(supermanConfig.getUrl() + "?" + request.toString(), orderFromYouzanEntity.getId());
        ResponseEntity<Map> responseEntity;
        Map<String, Object> result;
        try {
            orderFromYouzanEntity.setLastRechargeTime(new Date());
            // 将签名添加到URL参数后
            responseEntity = restTemplate.postForEntity(supermanConfig.getUrl(), request, Map.class);
            orderRequestRecordEntity.setResponse(responseEntity.getBody().toString());
            result = responseEntity.getBody();
        } catch (Exception e) {
            // 请求异常直接记录，然后就返回。等待定时器重试
            orderRequestRecordEntity.setException(e.getMessage());
            orderRequestRecordService.update(orderRequestRecordEntity);
            return;
        }

        // 受理成功和处理中先不管，失败就置为失败
        if ("0".equals(responseEntity.getBody().get("study").toString())) {
            orderRequestRecordEntity.setException(responseEntity.getBody().toString());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderRequestRecordService.update(orderRequestRecordEntity);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "超人平台受理失败"));
            return;
        }

        // 受理成功和处理中先不管，失败就置为失败
        if (!"8888".equals(responseEntity.getBody().get("code").toString())) {
            orderRequestRecordEntity.setException(responseEntity.getBody().toString());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderRequestRecordService.update(orderRequestRecordEntity);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "超人平台受理失败"));
            return;
        }

        // 如果没有订单价格，就把充值平台的成本价写上去
        if (orderFromYouzanEntity.getOrderPrice().intValue() == 0) {
            orderFromYouzanEntity.setOrderPrice(BigDecimal.valueOf(Long.valueOf((result.get("money")).toString())));
        }
        // 福禄平台已经受理订单，改变订单为受理中（等待通知或者在主动定时查询中处理）
        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
        orderRequestRecordService.update(orderRequestRecordEntity);
        orderFromYouzanService.update(orderFromYouzanEntity);
    }

}
