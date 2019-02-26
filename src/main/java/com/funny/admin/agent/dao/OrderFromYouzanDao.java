package com.funny.admin.agent.dao;


import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.system.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * 有赞已支付成功订单
 *
 * @author liyanjun
 * @date 2019-02-25 10:45:40
 */
public interface OrderFromYouzanDao extends BaseDao<OrderFromYouzanEntity> {

    OrderFromYouzanEntity queryObject(@Param("id") Integer id, @Param("isLock") boolean isLock);
}
