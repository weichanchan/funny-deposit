package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.entity.WareInfoVO;
import com.funny.admin.system.dao.BaseDao;

import java.util.List;
import java.util.Map;

/**
 * 商品信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-04 11:13:01
 */
public interface WareInfoDao extends BaseDao<WareInfoEntity> {
    /**
     * 根据商品编号查询商品
     *
     * @param wareNo
     * @return
     */
    WareInfoEntity queryObjectByWareNo(String wareNo);

    List<WareInfoVO> queryListAvailable(Map<String, Object> map);

    /**
     * 商品下架
     * @param ids
     */
    void offShelves(Long[] ids);

    /**
     * 根据商品编号查询商品库存
     * @param wareNo
     * @return
     */
    WareInfoVO queryObjectAvailable(String wareNo);
    /**
     * 商品上下架
     * @param map
     */
    void shelves(Map<String,Object> map);
}
