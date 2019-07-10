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

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private WareFuluInfoService wareFuluInfoService;

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
        ThridPlatformGateEntity thridPlatformGateEntity = thridPlatformGateService.queryObject(gate);
        if (thridPlatformGateEntity.getStatus() == ThridPlatformGateEntity.STATUS_CLOSE) {
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
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(aConfig.getCheckUrl() + "?" + request, String.class);
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
    }

}
