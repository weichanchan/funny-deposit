package com.funny.api.event;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class AgentOrderListener implements ApplicationListener<AgentOrderNotifyEvent> {
    Logger logger = LoggerFactory.getLogger(AgentOrderListener.class);

    @Autowired
    AgentOrderService agentOrderService;

    @Autowired
    RestTemplate template;

    @Autowired
    ConfigUtils configUtils;

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

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>(12);

        // 出错时订单通知返回
        if (!StringUtils.isBlank(agentOrderNotifyEvent.getJdReturnCode())) {
            //有错误码返回应该直接返回错误信息
            map.put("isSuccess", Collections.singletonList("F"));
            map.put("errorCode", Collections.singletonList(agentOrderNotifyEvent.getJdReturnCode()));
            String notifyUrl = agentOrderEntity.getNotifyUrl();
            ResponseEntity<Map> response = template.postForEntity(notifyUrl, map, Map.class);
            logger.info(response.toString());
            return;
        }

        // 处理成功时订单通知返回
        if (!StringUtils.isEmpty(agentOrderNotifyEvent.getCardInfoString())) {
            //卡信息加密
            String cardInfoString = null;
            cardInfoString = EncryptUtil.encryptBase64(agentOrderEntity.getCardInfo(), configUtils.getSecretKey());
            map.put("cardInfo", Collections.singletonList(cardInfoString));
        }

        map.put("timestamp", Collections.singletonList(agentOrderEntity.getTimestamp()));
        map.put("version", Collections.singletonList(agentOrderEntity.getVersion()));

        map.put("agentId", Collections.singletonList(agentOrderNotifyEvent.getWareInfoEntity().getAgentId()));
        map.put("bussType", Collections.singletonList(configUtils.getBussType()));
        //京东指定业务编号
        map.put("jdOrderNo", Collections.singletonList(agentOrderEntity.getJdOrderNo()));
        map.put("agentOrderNo", Collections.singletonList(agentOrderEntity.getAgentOrderNo()));
        map.put("status", Collections.singletonList(agentOrderEntity.getRechargeStatus()));
        //提取结果时间
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf1.format(new Date());
        map.put("time", Collections.singletonList(time));
        map.put("quantity", Collections.singletonList(agentOrderEntity.getQuantity()));

        String sign = SignUtils.getSign(map.toSingleValueMap(), configUtils.getSecretKey());
        map.put("sign", Collections.singletonList(sign));
        map.put("signType", Collections.singletonList(agentOrderEntity.getSignType()));
        if(agentOrderEntity.getCardInfo() != null) {
            try {
                //map.put("cardInfo", Collections.singletonList(URLEncoder.encode(EncryptUtil.encryptBase64(agentOrderEntity.getCardInfo(), PropertiesContent.get("secretKey")), "UTF-8")));
                map.put("cardInfo", Collections.singletonList(URLEncoder.encode(EncryptUtil.encryptBase64(agentOrderEntity.getCardInfo(), configUtils.getSecretKey()), "UTF-8")));
                logger.debug(map.get("cardInfo").iterator().next().toString());
            } catch (UnsupportedEncodingException e) {
                logger.error("cardInfo url编码失败");
            }
        }
        String notifyUrl = agentOrderEntity.getNotifyUrl();
        ResponseEntity<Map> response = template.postForEntity(notifyUrl, map, Map.class);
        logger.info(response.toString());
    }

}
