package com.funny.admin.agent.service;


import com.funny.admin.agent.entity.OrderRequestRecordEntity;

import java.util.List;
import java.util.Map;

/**
 * 卡门平台下单通讯记录
 * 
 * @author liyanjun
 * @date 2019-02-25 10:45:41
 */
public interface OrderRequestRecordService {
	
	OrderRequestRecordEntity queryObject(Integer id);
	
	List<OrderRequestRecordEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(OrderRequestRecordEntity orderRequestRecord);
	
	void update(OrderRequestRecordEntity orderRequestRecord);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);

    OrderRequestRecordEntity saveRequest(String request, Integer id);

	Integer queryTotalByOrderNo(Integer orderNo);
}
