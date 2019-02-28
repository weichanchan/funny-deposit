package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.WareFuluInfoEntity;
import com.funny.admin.system.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * 商品信息表
 * 
 * @author weicc
 * @email 
 * @date 2019-02-27 09:05:27
 */
public interface WareFuluInfoDao extends BaseDao<WareFuluInfoEntity> {

    WareFuluInfoEntity queryObject(@Param("id") Long id,@Param("isLock") boolean b);

    WareFuluInfoEntity queryByOuterSkuId(String outerSkuId);
}
