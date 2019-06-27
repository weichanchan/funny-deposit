package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.OrderRequestRecordDao;
import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.agent.service.OrderRequestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;




@Service("orderRequestRecordService")
@Transactional(rollbackFor = Exception.class)
public class OrderRequestRecordServiceImpl implements OrderRequestRecordService {
	@Autowired
	private OrderRequestRecordDao orderRequestRecordDao;
	
	@Override
	public OrderRequestRecordEntity queryObject(Integer id){
		return orderRequestRecordDao.queryObject(id);
	}
	
	@Override
	public List<OrderRequestRecordEntity> queryList(Map<String, Object> map){
		return orderRequestRecordDao.queryList(map);
	}
	
	@Override
	public int queryTotal(Map<String, Object> map){
		return orderRequestRecordDao.queryTotal(map);
	}
	
	@Override
	public void save(OrderRequestRecordEntity orderRequestRecord){
		orderRequestRecordDao.save(orderRequestRecord);
	}
	
	@Override
	public void update(OrderRequestRecordEntity orderRequestRecord){
		orderRequestRecordDao.update(orderRequestRecord);
	}
	
	@Override
	public void delete(Integer id){
		orderRequestRecordDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Integer[] ids){
		orderRequestRecordDao.deleteBatch(ids);
	}

	@Override
	public OrderRequestRecordEntity saveRequest(String request, Integer id) {
		OrderRequestRecordEntity orderRequestRecordEntity = new OrderRequestRecordEntity();
		orderRequestRecordEntity.setRequest(request);
		orderRequestRecordEntity.setCreateTime(new Date());
		orderRequestRecordEntity.setOrderNo(id);
		orderRequestRecordDao.save(orderRequestRecordEntity);
		return orderRequestRecordEntity;
	}

	@Override
	public Integer queryTotalByOrderNo(Integer orderNo) {
		return orderRequestRecordDao.queryTotalByOrderNo(orderNo);
	}

}
