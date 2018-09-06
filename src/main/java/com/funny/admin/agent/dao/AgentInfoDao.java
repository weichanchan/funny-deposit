package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.system.dao.BaseDao;

/**
 * 代理商信息表
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-05 18:04:57
 */
public interface AgentInfoDao extends BaseDao<AgentInfoEntity> {
    //根据代理商id查询代理商信息
    AgentInfoEntity queryObjectByAgentId(String agentId);
}
