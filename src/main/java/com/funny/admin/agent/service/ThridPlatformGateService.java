package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.ThridPlatformGateEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author weicc
 * @email 
 * @date 2019-07-10 16:39:52
 */
public interface ThridPlatformGateService {
	
	ThridPlatformGateEntity queryObject(Integer id);
	
	List<ThridPlatformGateEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(ThridPlatformGateEntity thridPlatformGate);
	
	void update(ThridPlatformGateEntity thridPlatformGate);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);
}
