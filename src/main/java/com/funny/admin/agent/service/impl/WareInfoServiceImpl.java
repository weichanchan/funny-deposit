package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.WareInfoDao;
import com.funny.admin.agent.dao.WareRoleDao;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.entity.WareInfoVO;
import com.funny.admin.agent.entity.WareRoleEntity;
import com.funny.admin.agent.service.WareInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl implements WareInfoService {
    @Autowired
    private WareInfoDao wareInfoDao;
    @Autowired
    private WareRoleDao wareRoleDao;

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
    public List<WareInfoVO> queryListAvailable(Map<String, Object> map) {
        return wareInfoDao.queryListAvailable(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return wareInfoDao.queryTotal(map);
    }

    @Override
    public void save(WareInfoEntity wareInfo) {
        wareInfoDao.save(wareInfo);

        saveWareRoles(wareInfo);
    }

    @Override
    public void update(WareInfoEntity wareInfo) {
        wareRoleDao.deleteByWareId(wareInfo.getId());
        saveWareRoles(wareInfo);
        wareInfoDao.update(wareInfo);
    }

    private void saveWareRoles(WareInfoEntity wareInfo) {
        List<Long> roleIds = wareInfo.getRoleIdList();
        if(roleIds==null || roleIds.size()==0){
            return;
        }
        for (Long roleId : roleIds) {
            WareRoleEntity wareRoleEntity = new WareRoleEntity();
            wareRoleEntity.setRoleId(roleId);
            wareRoleEntity.setWareInfoId(wareInfo.getId());
            wareRoleDao.save(wareRoleEntity);
        }
    }

    @Override
    public void delete(Long id) {
        wareInfoDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        wareInfoDao.deleteBatch(ids);
    }

    @Override
    public void offShelves(Long[] ids) {
        wareInfoDao.offShelves(ids);
    }

    @Override
    public List<Long> queryRoleIdList(Long id) {
        return wareRoleDao.queryRoleIdList(id);
    }

    @Override
    public WareInfoVO queryObjectAvailable(String wareNo) {
        return wareInfoDao.queryObjectAvailable(wareNo);
    }

}
