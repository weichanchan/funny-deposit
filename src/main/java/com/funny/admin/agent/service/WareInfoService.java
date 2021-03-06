package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.entity.WareInfoVO;
import com.funny.api.praise.entity.MsgPushEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-04 11:13:01
 */
public interface WareInfoService {

    WareInfoEntity queryObject(Long id);

    WareInfoEntity queryObjectByWareNo(String wareNo);

    List<WareInfoEntity> queryList(Map<String, Object> map);

    List<WareInfoVO> queryListAvailable(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(WareInfoEntity wareInfo);

    void update(WareInfoEntity wareInfo);

    void delete(Long id);

    void deleteBatch(Long[] ids);

    void offShelves(Long[] ids);

    List<Long> queryRoleIdList(Long id);

    WareInfoVO queryObjectAvailable(String wareNo);

    void shelves(Map<String,Object> map);

    String getPasscode(MsgPushEntity entity, Map<String, Object> orderInfo, List<Map<String, Object>> orders);
}
