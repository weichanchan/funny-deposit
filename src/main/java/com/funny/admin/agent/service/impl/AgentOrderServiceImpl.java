package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.AgentOrderDao;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.service.AgentOrderService;
import com.funny.api.event.AgentOrderNotifyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service("agentOrderService")
@Transactional(rollbackFor = Exception.class)
public class AgentOrderServiceImpl implements AgentOrderService {
    @Resource
    ApplicationContext applicationContext;
    @Autowired
    private AgentOrderDao agentOrderDao;

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
    public void handleSuccess(Long id) {
        AgentOrderEntity agentOrderEntity = agentOrderDao.queryObject(id);

        applicationContext.publishEvent(new AgentOrderNotifyEvent(id));
    }

    @Override
    public AgentOrderEntity queryLast() {
        return agentOrderDao.queryLastAgentOrder();
    }

}
