package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.AgentOrderDao;
import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.service.AgentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("agentOrderService")
public class AgentOrderServiceImpl implements AgentOrderService {
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

}
