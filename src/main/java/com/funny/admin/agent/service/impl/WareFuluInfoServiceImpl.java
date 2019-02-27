package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.entity.WareInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.funny.admin.agent.dao.WareFuluInfoDao;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.service.WareFuluInfoService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareFuluInfoService")
@Transactional(rollbackFor = Exception.class)
public class WareFuluInfoServiceImpl implements WareFuluInfoService {
	@Autowired
	private WareFuluInfoDao wareFuluInfoDao;


	@Override
	public WareFuluInfoEntity queryObject(Long id){
		return wareFuluInfoDao.queryObject(id,false);
	}
	@Override
	public WareFuluInfoEntity queryObject(Long id,boolean isLock){
		return wareFuluInfoDao.queryObject(id,isLock);
	}

	@Override
	public WareFuluInfoEntity queryByOuterSkuId(String outerSkuId) {
		return wareFuluInfoDao.queryByOuterSkuId(outerSkuId);
	}

	@Override
	public List<WareFuluInfoEntity> queryList(Map<String, Object> map){
		return wareFuluInfoDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return wareFuluInfoDao.queryTotal(map);
	}
	
	@Override
	public void save(WareFuluInfoEntity wareFuluInfo){
		wareFuluInfoDao.save(wareFuluInfo);
	}
	
	@Override
	public void update(WareFuluInfoEntity wareFuluInfo){
		wareFuluInfoDao.update(wareFuluInfo);
	}
	
	@Override
	public void delete(Long id){
		wareFuluInfoDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Long[] ids){
		wareFuluInfoDao.deleteBatch(ids);
	}
	
}
