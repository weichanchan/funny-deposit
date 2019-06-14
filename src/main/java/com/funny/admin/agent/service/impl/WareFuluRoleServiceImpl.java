package com.funny.admin.agent.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.funny.admin.agent.dao.WareFuluRoleDao;
import com.funny.admin.agent.entity.WareFuluRoleEntity;
import com.funny.admin.agent.service.WareFuluRoleService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareFuluRoleService")
@Transactional(rollbackFor = Exception.class)
public class WareFuluRoleServiceImpl implements WareFuluRoleService {
    @Autowired
    private WareFuluRoleDao wareFuluRoleDao;

    @Override
    public WareFuluRoleEntity queryObject(Long id) {
        return wareFuluRoleDao.queryObject(id, false);
    }

    @Override
    public List<WareFuluRoleEntity> queryList(Map<String, Object> map) {
        return wareFuluRoleDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return wareFuluRoleDao.queryTotal(map);
    }

    @Override
    public void save(WareFuluRoleEntity wareFuluRole) {
        wareFuluRoleDao.save(wareFuluRole);
    }

    @Override
    public void update(WareFuluRoleEntity wareFuluRole) {
        wareFuluRoleDao.update(wareFuluRole);
    }

    @Override
    public void delete(Long id) {
        wareFuluRoleDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        wareFuluRoleDao.deleteBatch(ids);
    }

    @Override
    public List<Long> queryRoleList(Long id) {
        return wareFuluRoleDao.queryRoleList(id);
    }

}
