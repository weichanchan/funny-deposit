package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.ThridPddConfigEntity;

import java.util.List;
import java.util.Map;

/**
 * 拼多多配置文件
 * 
 * @author weicc
 * @email 
 * @date 2019-07-26 10:28:38
 */
public interface ThridPddConfigService {
	
	ThridPddConfigEntity queryObject(Integer id);
	
	List<ThridPddConfigEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(ThridPddConfigEntity thridPddConfig);
	
	void update(ThridPddConfigEntity thridPddConfig);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);
}
