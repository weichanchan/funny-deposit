package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品信息表
 * 
 * @author weicc
 * @email 
 * @date 2019-02-27 09:05:27
 */
public interface WareFuluInfoService {
	
	WareFuluInfoEntity queryObject(Long id);
	
	List<WareFuluInfoEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(WareFuluInfoEntity wareFuluInfo);
	
	void update(WareFuluInfoEntity wareFuluInfo);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

	WareFuluInfoEntity queryObject(Long id, boolean b);

    WareFuluInfoEntity queryByOuterSkuId(String outerSkuId);
}
