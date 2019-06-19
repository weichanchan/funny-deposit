package com.funny.admin.agent.dao;


import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.system.dao.BaseDao;
import com.funny.utils.Query;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 有赞已支付成功订单
 *
 * @author liyanjun
 * @date 2019-02-25 10:45:40
 */
public interface OrderFromYouzanDao extends BaseDao<OrderFromYouzanEntity> {

    OrderFromYouzanEntity queryObject(@Param("id") Integer id, @Param("isLock") boolean isLock);

    OrderFromYouzanEntity lockByOrderNo(String customerOrderNo);

    BigDecimal queryTotalFee(Map<String, Object> query);
}
