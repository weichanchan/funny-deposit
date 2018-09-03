package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.system.dao.BaseDao;

/**
 * 代理商订单表
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-03 10:23:02
 */
public interface AgentOrderDao extends BaseDao<AgentOrderEntity> {

    Long getMaxId();
}
