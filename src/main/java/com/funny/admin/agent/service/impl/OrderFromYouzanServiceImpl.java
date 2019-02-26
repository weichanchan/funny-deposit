package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.OrderFromYouzanDao;
import com.funny.admin.agent.entity.OrderFromYouzanEntity;
import com.funny.admin.agent.service.OrderFromYouzanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service("orderFromYouzanService")
@Transactional(rollbackFor = Exception.class)
public class OrderFromYouzanServiceImpl implements OrderFromYouzanService {
    @Autowired
    private OrderFromYouzanDao orderFromYouzanDao;

    @Override
    public OrderFromYouzanEntity queryObject(Integer id) {
        return orderFromYouzanDao.queryObject(id, false);
    }

    @Override
    public OrderFromYouzanEntity queryObject(Integer id, boolean isLock) {
        return orderFromYouzanDao.queryObject(id, isLock);
    }

    @Override
    public List<OrderFromYouzanEntity> queryList(Map<String, Object> map) {
        return orderFromYouzanDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return orderFromYouzanDao.queryTotal(map);
    }

    @Override
    public void save(OrderFromYouzanEntity orderFromYouzan) {
        orderFromYouzanDao.save(orderFromYouzan);
    }

    @Override
    public void update(OrderFromYouzanEntity orderFromYouzan) {
        orderFromYouzanDao.update(orderFromYouzan);
    }

    @Override
    public void delete(Integer id) {
        orderFromYouzanDao.delete(id);
    }

    @Override
    public void deleteBatch(Integer[] ids) {
        orderFromYouzanDao.deleteBatch(ids);
    }

}
