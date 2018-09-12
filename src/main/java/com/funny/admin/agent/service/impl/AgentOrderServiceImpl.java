package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.AgentInfoDao;
import com.funny.admin.agent.dao.AgentOrderDao;
import com.funny.admin.agent.dao.CardInfoDao;
import com.funny.admin.agent.dao.WareInfoDao;
import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentInfoService;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.api.event.AgentOrderListener;
import com.funny.api.event.AgentOrderNotifyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("agentOrderService")
@Transactional(rollbackFor = Exception.class)
public class AgentOrderServiceImpl implements AgentOrderService {
    Logger logger = LoggerFactory.getLogger(AgentOrderListener.class);
    @Resource
    ApplicationContext applicationContext;

    @Autowired
    AgentOrderDao agentOrderDao;
    @Autowired
    AgentInfoDao agentInfoDao;
    @Autowired
    CardInfoDao cardInfoDao;
    @Autowired
    WareInfoDao wareInfoDao;

    @Override
    public AgentOrderEntity queryObject(Long id) {
        return agentOrderDao.queryObject(id);
    }

    @Override
    public AgentOrderEntity queryObjectByJdOrderNo(String jdOrderNo) {
        return agentOrderDao.queryObjectByJdOrderNo(jdOrderNo);
    }

    @Override
    public AgentOrderEntity queryObjectByAgentOrderNo(String agentOrderNo) {
        return agentOrderDao.queryObjectByAgentOrderNo(agentOrderNo);
    }

    @Override
    public List<AgentOrderEntity> queryList(Map<String, Object> map) {
        return agentOrderDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return agentOrderDao.queryTotal(map);
    }

    @Override
    public void save(AgentOrderEntity agentOrder) {
        agentOrderDao.save(agentOrder);
    }

    @Override
    public void update(AgentOrderEntity agentOrder) {
        agentOrderDao.update(agentOrder);
    }

    @Override
    public void delete(Long id) {
        agentOrderDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        agentOrderDao.deleteBatch(ids);
    }

    @Override
    public Long getMaxId() {
        return agentOrderDao.getMaxId();
    }

    @Override
    public List<AgentOrderEntity> updateBatch(int status, int rechargeStatus, Long[] ids) {
        return agentOrderDao.updateBatch(status, rechargeStatus, ids);
    }

    @Override
    public void handleSuccess(Long agentOrderId) {
        AgentOrderEntity agentOrderEntity = null;
        logger.debug("开始处理订单【" + agentOrderId + "】");
//        try {
        agentOrderEntity = agentOrderDao.queryObject(agentOrderId);

        String cardInfoString = "";
        String wareNo = agentOrderEntity.getWareNo();
        WareInfoEntity wareInfoEntity = wareInfoDao.queryObjectByWareNo(wareNo);
        if (wareInfoEntity != null) {

            String agentId = wareInfoEntity.getAgentId();
            AgentInfoEntity agentInfo = agentInfoDao.queryObjectByAgentId(agentId);
            if (agentInfo == null) {
                logger.error("商家不存在！");
                applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, null,AgentOrderNotifyEvent.JD00001));
                return;
            }

            Integer quantity = agentOrderEntity.getQuantity();
            if (wareInfoEntity.getType() == 1) {
                //客服已到到其他平台充值成功
                updateAgentOrder(agentOrderEntity, 3, 1);
                logger.info("充值成功！");
                applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, wareInfoEntity,""));
                return;
            }

            // TODO 这里应该移到下单那里，下单的时候，直接扣卡密库存，获取卡密串，然后发送通知事件
//            Map<String, Object> queryMap = new HashMap<>();
//            queryMap.put("wareNo", wareNo);
//            queryMap.put("num", quantity);
//            // Query query = new Query(queryMap);
//            List<CardInfoEntity> cardInfoLists = cardInfoDao.queryListNum(queryMap);
//            if (cardInfoLists == null || cardInfoLists.size() < quantity) {
//                //商品不可售
//                wareInfoEntity.setStatus(2);
//                wareInfoDao.update(wareInfoEntity);
//                logger.error("库存不足");
//                applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, wareInfoEntity,));
//                return;
//            }
//
//            String accountNo;
//            String password;
//            String expiryDate;
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//            //卡密类型充值
//            for (int i = 0; i < cardInfoLists.size(); i++) {
//                CardInfoEntity cardInfo = cardInfoLists.get(i);
//                //将卡密状态改为2：已使用，同时存入代理商订单编号
//                cardInfo.setStatus(2);
//                cardInfo.setAgentOrderNo(agentOrderEntity.getAgentOrderNo());
//                cardInfo.setRechargeTime(new Date());
//                cardInfoDao.update(cardInfo);
//                accountNo = cardInfo.getWareNo();
//                password = cardInfo.getPassword();
//                expiryDate = sdf.format(cardInfo.getExpiryDate());
//                if (i == 0) {
//                    cardInfoString += accountNo + "_" + password + "_" + expiryDate;
//                } else {
//                    cardInfoString += "|" + accountNo + "_" + password + "_" + expiryDate;
//                }
//            }
//            updateAgentOrder(agentOrderEntity, 3, 2);
//            applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, wareInfoEntity));
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
        agentOrderDao.update(agentOrderEntity);
    }

    @Override
    public AgentOrderEntity queryLast(Map<String, Object> params) {
        return agentOrderDao.queryLastAgentOrder(params);
    }

}
