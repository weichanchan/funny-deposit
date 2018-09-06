package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.WareInfoDao;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.service.WareInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl implements WareInfoService {
    @Autowired
    private WareInfoDao wareInfoDao;

    @Override
    public WareInfoEntity queryObject(Long id) {
        return wareInfoDao.queryObject(id);
    }

    @Override
    public WareInfoEntity queryObjectByWareNo(String wareNo) {
        return wareInfoDao.queryObjectByWareNo(wareNo);
    }

    @Override
    public List<WareInfoEntity> queryList(Map<String, Object> map) {
        return wareInfoDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return wareInfoDao.queryTotal(map);
    }

    @Override
    public void save(WareInfoEntity wareInfo) {
        wareInfoDao.save(wareInfo);
    }

    @Override
    public void update(WareInfoEntity wareInfo) {
        wareInfoDao.update(wareInfo);
    }

    @Override
    public void delete(Long id) {
        wareInfoDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        wareInfoDao.deleteBatch(ids);
    }

}
