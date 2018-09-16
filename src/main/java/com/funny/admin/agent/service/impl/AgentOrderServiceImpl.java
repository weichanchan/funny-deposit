package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.AgentInfoDao;
import com.funny.admin.agent.dao.AgentOrderDao;
import com.funny.admin.agent.dao.CardInfoDao;
import com.funny.admin.agent.dao.WareInfoDao;
import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.AgentOrderVO;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.admin.agent.service.CardInfoService;
import com.funny.api.event.AgentOrderListener;
import com.funny.api.event.AgentOrderNotifyEvent;
import com.funny.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
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
    @Autowired
    CardInfoService cardInfoService;
    @Autowired
    ConfigUtils configUtils;

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
    public List<AgentOrderVO> queryList1(Map<String, Object> map) {
        return agentOrderDao.queryList1(map);
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

        agentOrderEntity = agentOrderDao.queryObject(agentOrderId);
        if(agentOrderEntity == null){
            logger.error("此订单不存在");
            applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, null, AgentOrderNotifyEvent.JD00006));
            return;
        }

        String wareNo = agentOrderEntity.getWareNo();
        WareInfoEntity wareInfoEntity = wareInfoDao.queryObjectByWareNo(wareNo);
        if(wareInfoEntity == null){
            logger.error("参数错误！");
            applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, null, AgentOrderNotifyEvent.JD00003));
            return;
        }

        String agentId = wareInfoEntity.getAgentId();
        AgentInfoEntity agentInfo = agentInfoDao.queryObjectByAgentId(agentId);
        if (agentInfo == null) {
            logger.error("商家不存在！");
            applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, null, AgentOrderNotifyEvent.JD00001));
            return;
        }

        if(!configUtils.getAgentId().equals(agentId)){
            logger.error("代理商ID不正确！");
            applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, null, AgentOrderNotifyEvent.JD00007));
            return;
        }

        updateAgentOrder(agentOrderEntity, 3, 1);
        logger.info("充值成功！");
        applicationContext.publishEvent(new AgentOrderNotifyEvent(agentOrderId, agentOrderEntity, null, wareInfoEntity, ""));
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

    @Override
    public void handleFailed(Long id) {
        AgentOrderEntity agentOrderEntity = agentOrderDao.queryObject(id);
        agentOrderEntity.setStatus(3);
        agentOrderEntity.setRechargeStatus(2);
        agentOrderEntity.setHandleTime(new Date());
        agentOrderDao.update(agentOrderEntity);
        logger.error("商品不可售");
        applicationContext.publishEvent(new AgentOrderNotifyEvent(id, null, null, null, AgentOrderNotifyEvent.JDI00004));
        return;
    }

    @Override
    public void startHandle(Long id) {
        AgentOrderEntity agentOrderEntity = agentOrderDao.queryObject(id);
        agentOrderEntity.setStatus(2);
        agentOrderEntity.setRechargeStatus(3);
        agentOrderEntity.setHandleTime(new Date());
        agentOrderDao.update(agentOrderEntity);
//        logger.error("充值中");
//        applicationContext.publishEvent(new AgentOrderNotifyEvent(id, null, null, null, AgentOrderNotifyEvent.JDO00000));
//        return;
    }

}
