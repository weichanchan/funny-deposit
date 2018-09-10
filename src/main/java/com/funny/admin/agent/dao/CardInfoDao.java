package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.system.dao.BaseDao;

import java.util.List;
import java.util.Map;


/**
 * 卡密信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-07 13:33:51
 */
public interface CardInfoDao extends BaseDao<CardInfoEntity> {
    List<CardInfoEntity> queryColumns();

    //根据商品编号查询前 num 条数据
    List<CardInfoEntity> queryListNum(Map<String, Object> num);
}
