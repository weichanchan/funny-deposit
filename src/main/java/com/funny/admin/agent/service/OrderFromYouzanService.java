package com.funny.admin.agent.service;


import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.utils.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 有赞已支付成功订单
 * 
 * @author liyanjun
 * @date 2019-02-25 10:45:40
 */
public interface OrderFromYouzanService {
	
	OrderFromYouzanEntity queryObject(Integer id);

	OrderFromYouzanEntity queryObject(Integer id, boolean isLock);

	List<OrderFromYouzanEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(OrderFromYouzanEntity orderFromYouzan);
	
	void update(OrderFromYouzanEntity orderFromYouzan);
	
	void delete(Integer id);
	
	void deleteBatch(Integer[] ids);

    OrderFromYouzanEntity lockByOrderNo(String customerOrderNo);

    BigDecimal queryTotalFee(Map<String, Object> query);
}
