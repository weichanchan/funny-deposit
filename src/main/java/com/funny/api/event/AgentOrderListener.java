package com.funny.api.event;

import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentInfoService;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.utils.AESUtils;
import com.funny.utils.Query;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import sun.management.resources.agent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Transactional(rollbackFor = Exception.class)
public class AgentOrderListener implements ApplicationListener<AgentOrderNotifyEvent> {
    Logger logger = LoggerFactory.getLogger(AgentOrderListener.class);
    @Autowired
    AgentOrderService agentOrderService;
    @Autowired
    AgentInfoService agentInfoService;
    @Autowired
    CardInfoService cardInfoService;
    @Autowired
    WareInfoService wareInfoService;

    @Async
    @Override
    public void onApplicationEvent(AgentOrderNotifyEvent agentOrderNotifyEvent) {
        Long agentOrderId = Long.parseLong(agentOrderNotifyEvent.getSource().toString());
        AgentOrderEntity agentOrderEntity = null;
        logger.debug("开始处理订单【" + agentOrderId + "】");
        try {
            agentOrderEntity = agentOrderService.queryObject(agentOrderId);

            String cardInfoString = "";
            String wareNo = agentOrderEntity.getWareNo();
            WareInfoEntity wareInfoEntity = wareInfoService.queryObjectByWareNo(wareNo);
            if (wareInfoEntity != null) {

                String agentId = wareInfoEntity.getAgentId();
                AgentInfoEntity agentInfo = agentInfoService.queryObjectByAgentId(agentId);
                if (agentInfo == null) {
                    logger.error("商家不存在！");
                    return;
                }

                Integer quantity = agentOrderEntity.getQuantity();
                if (wareInfoEntity.getType() == 1) {
                    //客服到其他平台充值
                    updateAgentOrder(agentOrderEntity, 3, 2);
                    logger.info("充值成功！");
                    return;
                }

                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("wareNo", wareNo);
                queryMap.put("num", quantity);
                Query query = new Query(queryMap);
                List<CardInfoEntity> cardInfoLists = cardInfoService.queryListNum(query);
                if (cardInfoLists == null || cardInfoLists.size() < quantity) {
                    // TODO: 2018/9/10  库存不足该怎么处理，订单标记已处理，充值失败？
                    logger.error("库存不足");
                    return;
                }

                String accountNo, password, expiryDate;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                //卡密类型充值
                for (int i = 0; i < cardInfoLists.size(); i++) {
                    CardInfoEntity cardInfo = cardInfoLists.get(i);
                    //将卡密状态改为2：已使用，同时存入代理商订单编号
                    cardInfo.setStatus(2);
                    cardInfo.setAgentOrderNo(agentOrderEntity.getAgentOrderNo());
                    cardInfo.setRechargeTime(new Date());
                    cardInfoService.update(cardInfo);
                    accountNo = cardInfo.getWareNo();
                    password = cardInfo.getPassword();
                    expiryDate = sdf.format(cardInfo.getExpiryDate());
                    if (i == 0) {
                        cardInfoString += accountNo + "_" + password + "_" + expiryDate;
                    } else {
                        cardInfoString += "|" + accountNo + "_" + password + "_" + expiryDate;
                    }
                }
                updateAgentOrder(agentOrderEntity, 3, 2);

                cardInfoString = AESUtils.parseByte2HexStr(cardInfoString.getBytes());
                Map map = new HashMap();
                map.put("sign", agentOrderEntity.getSign());
                map.put("signType", agentOrderEntity.getSignType());
                map.put("timestamp", agentOrderEntity.getTimestamp());
                map.put("version", agentOrderEntity.getVersion());

                map.put("agentId", wareInfoEntity.getAgentId());
                map.put("bussType", "13758");
                //京东指定业务编号
                map.put("jdOrderNo", agentOrderEntity.getJdOrderNo());
                map.put("agentOrderNo", agentOrderEntity.getAgentOrderNo());
                map.put("status", agentOrderEntity.getStatus());
                //提取结果时间
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
                String time = sdf1.format(new Date());
                map.put("time", time);
                map.put("quantity", agentOrderEntity.getQuantity());
                map.put("cardInfo", cardInfoString);
                JSONObject jsonObj = JSONObject.fromObject(map);

                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                headers.add("Accept", MediaType.APPLICATION_JSON.toString());

                HttpEntity<String> formEntity = new HttpEntity<>(jsonObj.toString(), headers);
                String notifyUrl = agentOrderEntity.getNotifyUrl();

                RestTemplate template = new RestTemplate();
                // TODO: 2018/9/9  回调函数

                String response = template.postForObject(notifyUrl, formEntity, String.class);
                logger.info(response);
            }

        } catch (Exception e) {
            //JDO_10009	 订单处理失败
            logger.error("订单处理失败", e);
            if (agentOrderEntity != null) {
                agentOrderEntity.setStatus(3);
                agentOrderEntity.setRechargeStatus(3);
                agentOrderService.update(agentOrderEntity);
            }
        }
    }

    /**
     * 充值后, 更新订单状态
     *
     * @param agentOrderEntity
     */
    private void updateAgentOrder(AgentOrderEntity agentOrderEntity, Integer status, Integer rechargeStatus) {
        agentOrderEntity.setStatus(status);
        agentOrderEntity.setRechargeStatus(rechargeStatus);
        agentOrderEntity.setHandleTime(new Date());
        agentOrderService.update(agentOrderEntity);
    }
}
