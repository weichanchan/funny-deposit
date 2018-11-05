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
     * 根据商品编号和数量查询可用的卡密信息记录
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

    /**
     * 根据代理商订单号查询卡信息
     * @param agentOrderNo
     * @return
     */
    List<CardInfoEntity> queryListByAgentOrderNo(String agentOrderNo);

    int queryTotalNum(Map<String,Object> map);

    /**
     * 查询序列号是否已存在
     * @param map
     * @return
     */
    List<CardInfoEntity> queryListByPwds(Map<String, Object> map);

    /**
     *通过ids查询序列号
     * @param ids
     * @return
     */
    List<CardInfoEntity> queryListByIds(Long[] ids);

    /**
     * 批量更新卡密状态
     * @param ids
     */
    void updateStatusBatch(Long[] ids);
}
