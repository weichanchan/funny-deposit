package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.system.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * 代理商订单表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-03 10:23:02
 */
public interface AgentOrderDao extends BaseDao<AgentOrderEntity> {
    //获取订单表中的最大id
    Long getMaxId();

    //根据京东订单号查询代理商订单
    AgentOrderEntity queryObjectByJdOrderNo(String jdOrderNo);

    //根据代理商订单号查询代理商订单
    AgentOrderEntity queryObjectByAgentOrderNo(String agentOrderNo);

    //批量修改订单状态
    List<AgentOrderEntity> updateBatch(int status, int rechargeStatus, Long[] ids);

    /**
     * 查找该客服可以处理的最新的订单
     *
     * @param params
     * @return
     */
    AgentOrderEntity queryLastAgentOrder(Map<String, Object> params);
}
