package com.funny.admin.agent.dao;


import com.funny.admin.agent.entity.WareRoleEntity;
import com.funny.admin.system.dao.BaseDao;

import java.util.List;

/**
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-11 23:07:34
 */
public interface WareRoleDao extends BaseDao<WareRoleEntity> {

    /**
     * 根据id查询角色
     *
     * @param id
     * @return
     */
    List<Long> queryRoleIdList(Long id);

    void deleteByWareId(Long id);
}
