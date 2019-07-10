package com.funny.admin.agent.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.funny.admin.agent.dao.ThridPlatformGateDao;
import com.funny.admin.agent.entity.ThridPlatformGateEntity;
import com.funny.admin.agent.service.ThridPlatformGateService;
import org.springframework.transaction.annotation.Transactional;


@Service("thridPlatformGateService")
@Transactional(rollbackFor = Exception.class)
public class ThridPlatformGateServiceImpl implements ThridPlatformGateService {
	@Autowired
	private ThridPlatformGateDao thridPlatformGateDao;
	
	@Override
	public ThridPlatformGateEntity queryObject(Integer id){
		return thridPlatformGateDao.queryObject(id);
	}
	
	@Override
	public List<ThridPlatformGateEntity> queryList(Map<String, Object> map){
		return thridPlatformGateDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return thridPlatformGateDao.queryTotal(map);
	}
	
	@Override
	public void save(ThridPlatformGateEntity thridPlatformGate){
		thridPlatformGateDao.save(thridPlatformGate);
	}
	
	@Override
	public void update(ThridPlatformGateEntity thridPlatformGate){
		thridPlatformGateDao.update(thridPlatformGate);
	}
	
	@Override
	public void delete(Integer id){
		thridPlatformGateDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Integer[] ids){
		thridPlatformGateDao.deleteBatch(ids);
	}
	
}
