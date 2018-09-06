package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.system.dao.BaseDao;

/**
 * 商品信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-04 11:13:01
 */
public interface WareInfoDao extends BaseDao<WareInfoEntity> {
    //根据商品编号查询商品
    WareInfoEntity queryObjectByWareNo(String wareNo);
}
