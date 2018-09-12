package com.funny.api.event;

import com.funny.admin.agent.dao.AgentOrderDao;
import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentInfoService;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.AESUtils;
import com.funny.utils.SignUtils;
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

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Transactional(rollbackFor = Exception.class)
public class AgentOrderListener implements ApplicationListener<AgentOrderNotifyEvent> {
    Logger logger = LoggerFactory.getLogger(AgentOrderListener.class);

    @Autowired
    AgentOrderService agentOrderService;


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
        // TODO 找不到订单报错

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>(12);

        // 出错是订单通知返回
        if (StringUtils.isBlank(agentOrderNotifyEvent.getJdReturnCode())) {
            //TODO 有错误码返回应该直接返货错误信息
            map.put("isSuccess", Collections.singletonList("F"));
            map.put("errorCode", Collections.singletonList(agentOrderNotifyEvent.getJdReturnCode()));
            String notifyUrl = agentOrderEntity.getNotifyUrl();
            //TODO 不要每次都new一个restTemplate,可以在spring配置文件那里配置一个，然后autoWrit到每个地方使用。
            RestTemplate template = new RestTemplate();
            ResponseEntity<Map> response = template.postForEntity(notifyUrl, map, Map.class);
            logger.info(response.toString());
            return;
        }

        // 处理成功是订单通知返回
        if (!StringUtils.isEmpty(agentOrderNotifyEvent.getCardInfoString())) {
            String cardInfoString = AESUtils.parseByte2HexStr(agentOrderNotifyEvent.getCardInfoString().getBytes());
            map.put("cardInfo", Collections.singletonList(cardInfoString));
        }
        map.put("isSuccess", Collections.singletonList("T"));
        map.put("signType", Collections.singletonList(agentOrderEntity.getSignType()));
        map.put("timestamp", Collections.singletonList(agentOrderEntity.getTimestamp()));
        map.put("version", Collections.singletonList(agentOrderEntity.getVersion()));

        map.put("agentId", Collections.singletonList(agentOrderNotifyEvent.getWareInfoEntity().getAgentId()));
        // TODO bussType这个应该抽到一个公共配置类，不然到时候改起来麻烦
        map.put("bussType", Collections.singletonList("13758"));
        //京东指定业务编号
        map.put("jdOrderNo", Collections.singletonList(agentOrderEntity.getJdOrderNo()));
        map.put("agentOrderNo", Collections.singletonList(agentOrderEntity.getAgentOrderNo()));
        map.put("status", Collections.singletonList(agentOrderEntity.getRechargeStatus()));
        //提取结果时间
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf1.format(new Date());
        map.put("time", Collections.singletonList(time));
        map.put("quantity", Collections.singletonList(agentOrderEntity.getQuantity()));

        String sign = SignUtils.getSign(map.toSingleValueMap(), SignUtils.secretKeyOfFunny);
        String param = "";
        map.put("sign", Collections.singletonList(sign));
        String notifyUrl = agentOrderEntity.getNotifyUrl();
        //TODO 不要每次都new一个restTemplate,可以在spring配置文件那里配置一个，然后autoWrit到每个地方使用。
        RestTemplate template = new RestTemplate();
        ResponseEntity<Map> response = template.postForEntity(notifyUrl, map, Map.class);
        logger.info(response.toString());
    }

}
