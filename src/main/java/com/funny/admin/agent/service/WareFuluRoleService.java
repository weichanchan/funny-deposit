package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.WareFuluRoleEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author weicc
 * @email 
 * @date 2019-06-12 10:52:06
 */
public interface WareFuluRoleService {
	
	WareFuluRoleEntity queryObject(Long id);
	
	List<WareFuluRoleEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(WareFuluRoleEntity wareFuluRole);
	
	void update(WareFuluRoleEntity wareFuluRole);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	List<Long> queryRoleList(Long id);
}
