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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
    protected AConfig aConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(ASubmitEvent aSubmitEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(aSubmitEvent.getSource()));
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
        Map<String,String> map = new HashMap();
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
        map.put("chargeNo", String.valueOf(objectMapper.readValue(orderFromYouzanEntity.getRechargeInfo(), Map.class).get(wareFuluInfoEntity.getMark())));

        // 发送请求并记录
        String sign = SignUtils.getASign(map, aConfig.getAppKey());
        String request = SignUtils.MaptoString(map) + "&signType=md5&sign=" + sign;
        OrderRequestRecordEntity orderRequestRecordEntity = orderRequestRecordService.saveRequest(aConfig.getUrl() + "?" + request, orderFromYouzanEntity.getId());
        ResponseEntity<String> responseEntity;
        Map<String,Object> result;
        try {
            orderFromYouzanEntity.setLastRechargeTime(new Date());
            responseEntity = restTemplate.getForEntity(aConfig.getUrl() + "?" + request, String.class);
            orderRequestRecordEntity.setResponse(responseEntity.getBody());
            result = objectMapper.readValue(responseEntity.getBody(), Map.class);
        } catch (Exception e) {
            // 请求异常直接记录，然后就返回。等待定时器重试
            orderRequestRecordEntity.setException(e.getMessage());
            orderRequestRecordService.update(orderRequestRecordEntity);
            return;
        }

        if (!"0".equals(result.get("resultCode").toString())) {
            orderRequestRecordEntity.setException(responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderRequestRecordService.update(orderRequestRecordEntity);
            orderFromYouzanService.update(orderFromYouzanEntity);
            applicationContext.publishEvent(new YouzanRefundEvent(orderFromYouzanEntity.getId(), "A平台受理失败"));
            return;
        }

        // 福禄平台已经受理订单，改变订单为受理中（等待通知或者在主动定时查询中处理）
        orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.PROCESS);
        orderRequestRecordService.update(orderRequestRecordEntity);
        orderFromYouzanService.update(orderFromYouzanEntity);
    }

}
