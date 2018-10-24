package com.funny.api.event.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.utils.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AgentOrderListener implements ApplicationListener<AgentOrderNotifyEvent> {
    Logger logger = LoggerFactory.getLogger(AgentOrderListener.class);

    @Autowired
    AgentOrderService agentOrderService;

    @Autowired
    RestTemplate template;

    @Autowired
    ConfigUtils configUtils;

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 本监听仅做 Http 通知使用，这里最多查询数据库，不应该修改数据库里面的内容
     *
     * @param agentOrderNotifyEvent
     */
    @Async
    @Override
    public void onApplicationEvent(AgentOrderNotifyEvent agentOrderNotifyEvent) {
        Long agentOrderId = Long.valueOf(agentOrderNotifyEvent.getSource().toString());
        AgentOrderEntity agentOrderEntity = agentOrderService.queryObject(agentOrderId);

        Map<String, Object> map = new HashMap<>(12);

        // 订单通知返回
        if (!StringUtils.isEmpty(agentOrderEntity.getCardInfo())) {
            //卡信息加密
            String cardInfoString = null;
            cardInfoString = EncryptUtil.encryptBase64(agentOrderEntity.getCardInfo(), configUtils.getSecretKey());
            map.put("cardInfo", cardInfoString);
        }

        map.put("timestamp", agentOrderEntity.getTimestamp());
        map.put("version", agentOrderEntity.getVersion());

        map.put("agentId",configUtils.getAgentId());
        map.put("bussType", configUtils.getBussType());
        //京东指定业务编号
        map.put("jdOrderNo", agentOrderEntity.getJdOrderNo());
        map.put("agentOrderNo", agentOrderEntity.getAgentOrderNo());
        map.put("status", agentOrderEntity.getRechargeStatus());
        //提取结果时间
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf1.format(new Date());
        map.put("time", time);
        map.put("quantity", agentOrderEntity.getQuantity());
        if (agentOrderEntity.getCardInfo() != null) {
            try {
                map.put("cardInfo", URLEncoder.encode(EncryptUtil.encryptBase64(agentOrderEntity.getCardInfo(), configUtils.getSecretKey()), "UTF-8"));
                logger.debug(map.get("cardInfo").toString());
            } catch (UnsupportedEncodingException e) {
                logger.error("cardInfo url编码失败");
            }
        }
        String sign = SignUtils.getSign(map, configUtils.getSecretKey());
        map.put("sign", sign);
        map.put("signType", agentOrderEntity.getSignType());
        String notifyUrl = agentOrderEntity.getNotifyUrl();
        String param = "?";
        for (String key : map.keySet()) {
            param += key + "=" + map.get(key) + "&";
        }
        param = param.substring(0, param.length() - 1);
        ResponseEntity<String> response = template.postForEntity(notifyUrl + param, null, String.class);
        logger.debug(notifyUrl + param);
        try {
            logger.debug(response.getBody());
            Map result = objectMapper.readValue(response.getBody(), Map.class);
            String flag = (String) result.get("isSuccess");
            if ("T".equals(flag)) {
                return;
            }
        } catch (Exception e) {
            logger.error(response.getBody(), e);
        }
        // 记录重复记录到数据库重复
        agentOrderService.newResend(agentOrderEntity, notifyUrl + param);
    }

}
