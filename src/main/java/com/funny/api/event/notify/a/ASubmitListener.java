package com.funny.api.event.notify.a;

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
import com.funny.config.AConfig;
import com.funny.config.FuluConfig;
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
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 去福禄平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class ASubmitListener {

    private static final Logger logger = LoggerFactory.getLogger(ASubmitListener.class);

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private static final int gate = 2;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private OrderRequestRecordService orderRequestRecordService;

    @Autowired
    private WareFuluInfoService wareFuluInfoService;

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    protected AConfig aConfig;

    @Autowired
    private ThridPlatformGateService thridPlatformGateService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(ASubmitEvent aSubmitEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(aSubmitEvent.getSource()));
        logger.debug("平台开始发送：" + id);
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是待充值状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.WAIT_PROCESS) {
            logger.debug("平台发送失败订单状态不是待发送");
            return;
        }
        ThridPlatformGateEntity thridPlatformGateEntity = thridPlatformGateService.queryObject(gate);
        if (thridPlatformGateEntity.getStatus() == ThridPlatformGateEntity.STATUS_CLOSE) {
            orderFromYouzanEntity.setException("【A】渠道关闭。");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "充值渠道关闭。"));
            return;
        }
        WareFuluInfoEntity wareFuluInfoEntity = wareFuluInfoService.queryByOuterSkuId(orderFromYouzanEntity.getWareNo());
        if (wareFuluInfoEntity == null) {
            logger.debug("平台发送失败订单状态商品不可售");
            orderFromYouzanEntity.setException("商品不可售，退款。");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "商品不可售"));
            return;
        }
        Map<String, String> map = new HashMap();
        // 合作商家订单号（唯一不重复）
        map.put("outOrderNo", orderFromYouzanEntity.getOrderNo());
        map.put("mctNo", aConfig.getMctNo());
        map.put("prodNo", wareFuluInfoEntity.getProductId().toString());
        map.put("prodName", wareFuluInfoEntity.getWareName());
        // 计算购买数量
        Integer count = wareFuluInfoEntity.getNum() * orderFromYouzanEntity.getNum();
        // 购买数量
        map.put("amount", String.valueOf(count));
        map.put("province", "1");
        // 充值账号
        if (wareFuluInfoEntity.getMark() == null || "".equals(wareFuluInfoEntity.getMark())) {
            map.put("chargeNo", orderFromYouzanEntity.getRechargeInfo());
        } else {
            map.put("chargeNo", String.valueOf(objectMapper.readValue(orderFromYouzanEntity.getRechargeInfo(), Map.class).get(wareFuluInfoEntity.getMark())));
        }

        // 发送请求并记录
        String sign = SignUtils.getASign(map, aConfig.getAppKey());
        String request = SignUtils.MaptoString(map) + "&signType=md5&sign=" + sign;
        OrderRequestRecordEntity orderRequestRecordEntity = orderRequestRecordService.saveRequest(aConfig.getUrl() + "?" + request, orderFromYouzanEntity.getId());
        orderFromYouzanEntity.setLastRechargeTime(new Date());
        ListenableFuture<ResponseEntity<String>> forEntity = asyncRestTemplate.getForEntity(aConfig.getUrl() + "?" + request, String.class);
        forEntity.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                // 请求异常直接记录，然后就返回。等待定时器重试
                orderRequestRecordEntity.setException(throwable.getMessage());
                orderRequestRecordService.update(orderRequestRecordEntity);
                return;
            }

            @Override
            public void onSuccess(ResponseEntity<String> responseEntity) {
                orderRequestRecordEntity.setResponse(responseEntity.getBody());
                Map<String, Object> result = null;
                try {
                    result = objectMapper.readValue(responseEntity.getBody(), Map.class);
                } catch (IOException e) {
                    logger.error( "json 格式转化失败",e);
                }
                if (!"0".equals(result.get("resultCode").toString())) {
                    orderRequestRecordEntity.setException(responseEntity.getBody());
                    orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
                    orderRequestRecordService.update(orderRequestRecordEntity);
                    orderFromYouzanService.update(orderFromYouzanEntity);
                    applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "A平台受理失败"));
                    return;
                }
                // 如果没有订单价格，就把充值平台的成本价写上去
                if (orderFromYouzanEntity.getOrderPrice().intValue() == 0) {
                    orderFromYouzanEntity.setOrderPrice(BigDecimal.valueOf(Long.valueOf(((Map) result.get("resultData")).get("totalFee").toString())).
                            divide(BigDecimal.TEN).divide(BigDecimal.TEN));
                }
                // 福禄平台已经受理订单，改变订单为受理中（等待通知或者在主动定时查询中处理）
                orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
                orderRequestRecordService.update(orderRequestRecordEntity);
                orderFromYouzanService.update(orderFromYouzanEntity);
            }
        });


    }

}
