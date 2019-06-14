package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.WareFuluRoleEntity;
import com.funny.admin.system.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author weicc
 * @email 
 * @date 2019-06-12 10:52:06
 */
public interface WareFuluRoleDao extends BaseDao<WareFuluRoleEntity> {

    WareFuluRoleEntity queryObject(@Param("id") Long id, @Param("isLock") boolean b);

    List<Long> queryRoleList(Long id);

    void deleteByWareId(Long id);
}
