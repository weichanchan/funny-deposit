package com.funny.api.event.notify.superman;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.entity.ThridPlatformGateEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.OrderRequestRecordService;
import com.funny.admin.agent.service.ThridPlatformGateService;
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
import org.springframework.beans.BeanUtils;
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
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 去超人平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class SupermanSubmitListener {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private static final int gate = 3;

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

    @Autowired
    private ThridPlatformGateService thridPlatformGateService;

    @Async
    @EventListener
    public void onApplicationEvent(SupermanSubmitEvent supermanSubmitEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(supermanSubmitEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是待充值状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.WAIT_PROCESS) {
            return;
        }
        if (orderFromYouzanEntity.getLastRechargeTime() != null && orderFromYouzanEntity.getLastRechargeTime().after(new Date())) {
            // 还没到时间发
            return;
        }
        ThridPlatformGateEntity thridPlatformGateEntity = thridPlatformGateService.queryObject(gate);
        if (thridPlatformGateEntity.getStatus() == ThridPlatformGateEntity.STATUS_CLOSE) {
            orderFromYouzanEntity.setException("【超人】渠道关闭。");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "充值渠道关闭。"));
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
        MultiValueMap map = new LinkedMultiValueMap(16);
        map.put("user", Collections.singletonList(supermanConfig.getUsername()));
        // 这个平台不需要第三方订单号，问你怕不怕
//        map.put("order", Collections.singletonList(orderFromYouzanEntity.getOrderNo()));
        map.put("code", Collections.singletonList("102"));
        map.put("token", Collections.singletonList(supermanConfig.getToken()));
        map.put("pass", Collections.singletonList(SignUtils.getMD5(supermanConfig.getPassword())));
        map.put("spid", Collections.singletonList(wareFuluInfoEntity.getProductId()));
        // 除了Q币，超人的数量都为1
        Integer count = 1;
        if (wareFuluInfoEntity.getWareName().contains("Q币")
                || wareFuluInfoEntity.getWareName().contains("q币")
                || wareFuluInfoEntity.getWareName().contains("鱼翅")
                || wareFuluInfoEntity.getWareName().contains("陌陌币")
                || wareFuluInfoEntity.getWareName().contains("喜点")) {
            count = wareFuluInfoEntity.getNum() * orderFromYouzanEntity.getNum();
        }

        // 购买数量
        map.put("mun", Collections.singletonList(String.valueOf(count)));

        // 充值账号
        if (wareFuluInfoEntity.getMark() == null || "".equals(wareFuluInfoEntity.getMark())) {
            map.put("beizhu", Collections.singletonList(Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(Collections.singletonList(orderFromYouzanEntity.getRechargeInfo())).getBytes("UTF-8"))));
        } else {
            map.put("beizhu", Collections.singletonList(Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(Collections.singletonList(String.valueOf(objectMapper.readValue(orderFromYouzanEntity.getRechargeInfo(), Map.class).get(wareFuluInfoEntity.getMark())))).getBytes("UTF-8"))));
        }

        // 发送请求并记录
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        int i = orderRequestRecordService.queryTotalByOrderNo(orderFromYouzanEntity.getId());
        if (i > 0) {
            // 只发一次
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "超人平台受理失败"));
            return;
        }

        OrderRequestRecordEntity orderRequestRecordEntity = orderRequestRecordService.saveRequest(supermanConfig.getUrl() + "?" + request.toString(), orderFromYouzanEntity.getId());

        try {
            orderFromYouzanEntity.setLastRechargeTime(new Date());
            // 将签名添加到URL参数后
            ListenableFuture<ResponseEntity<Map>> forEntity = asyncRestTemplate.postForEntity(supermanConfig.getUrl(), request, Map.class);
            forEntity.addCallback(new ListenableFutureCallback<ResponseEntity<Map>>() {
                @Override
                public void onFailure(Throwable throwable) {
                    // 请求异常直接记录，然后就返回。等待定时器重试
                    orderRequestRecordEntity.setException(throwable.getMessage());
                    orderRequestRecordService.update(orderRequestRecordEntity);
                    return;
                }

                @Override
                public void onSuccess(ResponseEntity<Map> responseEntity) {
                    orderRequestRecordEntity.setResponse(responseEntity.getBody().toString());
                    Map<String, Object> result = responseEntity.getBody();
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
                        orderFromYouzanEntity.setOrderPrice(BigDecimal.valueOf(Double.valueOf((result.get("money")).toString())));
                    }
                    orderFromYouzanEntity.setOrderNo(orderFromYouzanEntity.getOrderNo() + "---" + result.get("order").toString());
                    // 福禄平台已经受理订单，改变订单为受理中（等待通知或者在主动定时查询中处理）
                    orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
                    orderRequestRecordService.update(orderRequestRecordEntity);
                    orderFromYouzanService.update(orderFromYouzanEntity);
                }
            });

        } catch (Exception e) {
            // 请求异常直接记录，然后就返回。等待定时器重试
            orderRequestRecordEntity.setException(e.getMessage());
            orderRequestRecordService.update(orderRequestRecordEntity);
            return;
        }
    }

}
