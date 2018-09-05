package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.AgentInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 代理商信息表
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-05 18:04:57
 */
public interface AgentInfoService {
	
	AgentInfoEntity queryObject(Long id);

	AgentInfoEntity queryObjectByAgentId(String agentId);

	List<AgentInfoEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(AgentInfoEntity agentInfo);
	
	void update(AgentInfoEntity agentInfo);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
}
