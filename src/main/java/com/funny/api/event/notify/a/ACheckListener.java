package com.funny.api.event.notify.a;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
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
public class ACheckListener {

    private static final Logger logger = LoggerFactory.getLogger(ACheckListener.class);

    @Autowired
    protected AConfig aConfig;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderFromYouzanService orderFromYouzanService;

    @Autowired
    private WareFuluInfoService wareFuluInfoService;

    @Autowired
    ApplicationContext applicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Async
    @EventListener
    public void onApplicationEvent(ACheckEvent fuluCheckEvent) throws IOException {
        Integer id = Integer.parseInt(String.valueOf(fuluCheckEvent.getSource()));
        OrderFromYouzanEntity orderFromYouzanEntity = orderFromYouzanService.queryObject(id, true);
        // 不是充值中状态，不处理
        if (orderFromYouzanEntity.getStatus() != OrderFromYouzanEntity.PROCESS) {
            return;
        }
        Map map = new HashMap();
        // 合作商家订单号（唯一不重复）
        map.put("outOrderNo", orderFromYouzanEntity.getOrderNo());

        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        // 将签名添加到URL参数后
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(aConfig.getCheckUrl(), request, String.class);
        Map<String, Object> result = objectMapper.readValue(responseEntity.getBody(), Map.class);
        logger.debug(responseEntity.getBody());
        if ("0".equals(result.get("resultCode").toString()) && "SUCCESS".equals(((Map) result.get("resultData")).get("status"))) {
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.SUCCESS);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }
        // 受理成功和处理中先不管，失败就置为失败
        if ("0".equals(result.get("resultCode").toString()) && "FAIL".equals(((Map) result.get("resultData")).get("status"))) {
            orderFromYouzanEntity.setException("充值失败：" + responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.FAIL);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }

        // 失败
        if (!"0".equals(result.get("resultCode").toString())) {
            orderFromYouzanEntity.setException("充值异常：" + responseEntity.getBody());
            orderFromYouzanEntity.setStatus(OrderFromYouzanEntity.EXCEPTION);
            orderFromYouzanService.update(orderFromYouzanEntity);
            return;
        }
    }

}
