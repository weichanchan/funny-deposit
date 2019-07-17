package com.funny.api.event.notify.a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.ThridPlatformGateEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import com.funny.admin.agent.service.ThridPlatformGateService;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.config.AConfig;
import com.funny.config.FuluConfig;
import com.funny.utils.DateUtils;
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
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 去福禄平台下单监听器
 *
 * @author liyanjun
 */
@Component
public class ACheckListener {

    private static final Logger logger = LoggerFactory.getLogger(ACheckListener.class);

    private static final int gate = 2;

    @Autowired
    protected AConfig aConfig;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private ThridPlatformGateService thridPlatformGateService;

    @Async
    @EventListener
    public void onApplicationEvent(ACheckEvent fuluCheckEvent) throws Exception {
        Integer id = Integer.parseInt(String.valueOf(fuluCheckEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是充值中状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.PROCESS) {
            return;
        }
        if (orderFromYouzanEntity.getNextRechargeTime() != null && orderFromYouzanEntity.getNextRechargeTime().after(new Date())) {
            // 还没到查询时间
            return;
        }
        ThridPlatformGateEntity thridPlatformGateEntity = thridPlatformGateService.queryObject(gate);
        if (thridPlatformGateEntity.getCheckStatus() == ThridPlatformGateEntity.STATUS_CLOSE) {
            logger.debug("A平台渠道关闭，先不查询");
            return;
        }
        Map map = new HashMap();
        // 合作商家订单号（唯一不重复）
        map.put("OrderNo", orderFromYouzanEntity.getOrderNo());
        map.put("cpid", aConfig.getMctNo());
        map.put("createtime", URLEncoder.encode(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN), "UTF-8"));
        // 发送请求
        String sign = SignUtils.getACheckSign(map, aConfig.getAppKey());
        String request = SignUtils.MaptoString(map) + "&sign=" + sign;
        // 将签名添加到URL参数后
        ListenableFuture<ResponseEntity<String>> forEntity = asyncRestTemplate.getForEntity(aConfig.getCheckUrl() + "?" + request, String.class);
        forEntity.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                recordCheckFail(orderFromYouzanEntity);
            }

            @Override
            public void onSuccess(ResponseEntity<String> responseEntity) {
                //创建一个DocumentBuilderFactory的对象
                logger.debug(responseEntity.getBody());
                // 受理成功和处理中先不管，失败就置为失败
                if (responseEntity.getBody().contains("ORDER_FAILEDL")) {
                    orderFromYouzanEntity.setException("充值失败：A平台通知失败");
                    orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
                    orderFromYouzanService.update(orderFromYouzanEntity);
                    return;
                }

                if (responseEntity.getBody().contains("SUCCESSL")) {
                    orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
                    orderFromYouzanService.update(orderFromYouzanEntity);
                    return;
                }

                recordCheckFail(orderFromYouzanEntity);
            }
        });

    }

    private void recordCheckFail(OrderFromYouzanEntity orderFromYouzanEntity) {
        logger.debug("订单【" + orderFromYouzanEntity.getId() + "】查询失败，检查是否重试");
        int count = orderFromYouzanEntity.getCount() + 1;
        if (count >= 5 ) {
            logger.debug("订单【" + orderFromYouzanEntity.getId() + "】重试到达上线");
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.CHECK_FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }
        orderFromYouzanEntity.setCount(count);
        orderFromYouzanEntity.setNextRechargeTime(new Date(orderFromYouzanEntity.getCreateTime().getTime() + count * count * 60 * 1000));
        logger.debug("订单【" + orderFromYouzanEntity.getId() + "】需要重试，下次重试时间" + orderFromYouzanEntity.getNextRechargeTime());
        orderFromYouzanService.update(orderFromYouzanEntity);
    }
}
