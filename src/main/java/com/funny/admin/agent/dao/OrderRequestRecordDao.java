package com.funny.admin.agent.dao;


import com.funny.admin.agent.entity.OrderRequestRecordEntity;
import com.funny.admin.system.dao.BaseDao;

/**
 * 卡门平台下单通讯记录
 * 
 * @author liyanjun
 * @date 2019-02-25 10:45:41
 */
public interface OrderRequestRecordDao extends BaseDao<OrderRequestRecordEntity> {

    Integer queryTotalByOrderNo(Integer orderNo);
}
