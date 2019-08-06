package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.WareFuluInfoDao;
import com.funny.admin.agent.dao.WareFuluRoleDao;
import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.agent.entity.WareFuluRoleEntity;
import com.funny.admin.agent.service.WareFuluInfoService;
import com.funny.admin.system.dao.SysRoleDao;
import com.funny.admin.system.entity.SysRoleEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service("wareFuluInfoService")
@Transactional(rollbackFor = Exception.class)
public class WareFuluInfoServiceImpl implements WareFuluInfoService {
    @Autowired
    private WareFuluInfoDao wareFuluInfoDao;
    @Autowired
    private WareFuluRoleDao wareFuluRoleDao;
    @Autowired
    private SysRoleDao sysRoleDao;


    @Override
    public WareFuluInfoEntity queryObject(Long id) {
        return wareFuluInfoDao.queryObject(id, false);
    }

    @Override
    public WareFuluInfoEntity queryObject(Long id, boolean isLock) {
        return wareFuluInfoDao.queryObject(id, isLock);
    }

    @Override
    public WareFuluInfoEntity queryByOuterSkuId(String outerSkuId) {
        return wareFuluInfoDao.queryByOuterSkuId(outerSkuId);
    }

    @Override
    public List<WareFuluInfoEntity> queryList(Map<String, Object> map) {
        return wareFuluInfoDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return wareFuluInfoDao.queryTotal(map);
    }

    @Override
    public void save(WareFuluInfoEntity wareFuluInfo) {
        wareFuluInfoDao.save(wareFuluInfo);
        wareFuluInfo.setRoleName(saveWareFuluRoles(wareFuluInfo));
        wareFuluInfoDao.update(wareFuluInfo);

    }

    private String saveWareFuluRoles(WareFuluInfoEntity wareFuluInfo) {
        String roleName = "";
        List<Long> roleIds = wareFuluInfo.getRoleList();
        if (roleIds == null || roleIds.size() == 0) {
            return "";
        }
        for (Long roleId : roleIds) {
            WareFuluRoleEntity wareFuluRoleEntity = new WareFuluRoleEntity();
            wareFuluRoleEntity.setRoleId(roleId);
            wareFuluRoleEntity.setWareFuluInfoId(wareFuluInfo.getId());
            wareFuluRoleDao.save(wareFuluRoleEntity);
            SysRoleEntity sysRole = sysRoleDao.queryObject(roleId);
            if (sysRole != null) {
                roleName += "、" + sysRoleDao.queryObject(roleId).getRoleName();
            }
        }
        if(StringUtils.isNotBlank(roleName)){
            return roleName.substring(1);
        }
        return "";
    }

    @Override
    public void update(WareFuluInfoEntity wareFuluInfo) {
        wareFuluRoleDao.deleteByWareId(wareFuluInfo.getId());
        wareFuluInfo.setRoleName(saveWareFuluRoles(wareFuluInfo));
        wareFuluInfoDao.update(wareFuluInfo);
    }

    @Override
    public void delete(Long id) {
        wareFuluInfoDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        wareFuluInfoDao.deleteBatch(ids);
    }

}
