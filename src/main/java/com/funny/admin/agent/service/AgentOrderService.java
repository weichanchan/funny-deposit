package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.AgentOrderEntity;

import java.util.List;
import java.util.Map;

/**
 * 代理商订单表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-03 10:23:02
 */
public interface AgentOrderService {

    AgentOrderEntity queryObject(Long id);

    AgentOrderEntity queryObjectByJdOrderNo(String jdOrderNo);

    AgentOrderEntity queryObjectByAgentOrderNo(String agentOrderNo);

    List<AgentOrderEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(AgentOrderEntity agentOrder);

    void update(AgentOrderEntity agentOrder);

    void delete(Long id);

    void deleteBatch(Long[] ids);

    Long getMaxId();

    List<AgentOrderEntity> updateBatch(int status, int rechargeStatus, Long[] ids);

    void handleSuccess(Long id);

    /**
     * 查询最新的订单
     *
     * @return
     * @param params
     */
    AgentOrderEntity queryLast(Map<String, Object> params);

    void handleFailed(Long id);

    void startHandle(Long id);
}
