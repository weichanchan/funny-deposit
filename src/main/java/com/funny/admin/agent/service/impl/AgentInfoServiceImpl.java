package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.AgentInfoDao;
import com.funny.admin.agent.entity.AgentInfoEntity;
import com.funny.admin.agent.service.AgentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



@Service("agentInfoService")
public class AgentInfoServiceImpl implements AgentInfoService {
	@Autowired
	private AgentInfoDao agentInfoDao;
	
	@Override
	public AgentInfoEntity queryObject(Long id){
		return agentInfoDao.queryObject(id);
	}

	@Override
	public AgentInfoEntity queryObjectByAgentId(String agentId) {
		return agentInfoDao.queryObjectByAgentId(agentId);
	}

	@Override
	public List<AgentInfoEntity> queryList(Map<String, Object> map){
		return agentInfoDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return agentInfoDao.queryTotal(map);
	}
	
	@Override
	public void save(AgentInfoEntity agentInfo){
		agentInfoDao.save(agentInfo);
	}
	
	@Override
	public void update(AgentInfoEntity agentInfo){
		agentInfoDao.update(agentInfo);
	}
	
	@Override
	public void delete(Long id){
		agentInfoDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Long[] ids){
		agentInfoDao.deleteBatch(ids);
	}
	
}
