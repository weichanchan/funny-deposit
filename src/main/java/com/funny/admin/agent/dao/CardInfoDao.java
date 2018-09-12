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

    /**
     * 根据商品编号查询前 num 条数据
     *
     * @param num
     * @return
     */
    List<CardInfoEntity> queryListNum(Map<String, Object> num);

    /**
     * 商品编号为给定值的卡密信息记录条数
     *
     * @param map
     * @return
     */
    int queryTotalByWareNo(Map<String, Object> map);

    /**
     * 根据商品编号查询数据
     *
     * @param map
     * @return
     */
    List<CardInfoEntity> queryListByWareNo(Map<String, Object> map);
}
