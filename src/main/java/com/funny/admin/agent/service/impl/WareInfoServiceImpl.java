package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.CardInfoDao;
import com.funny.admin.agent.dao.WareInfoDao;
import com.funny.admin.agent.dao.WareRoleDao;
import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import com.funny.admin.agent.entity.WareInfoVO;
import com.funny.admin.agent.entity.WareRoleEntity;
import com.funny.admin.agent.service.WareInfoService;
import com.funny.api.praise.entity.MsgPushEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("wareInfoService")
@Transactional(rollbackFor = Exception.class)
public class WareInfoServiceImpl implements WareInfoService {

    private static final Logger logger = LoggerFactory.getLogger(WareInfoServiceImpl.class);

    @Autowired
    private WareInfoDao wareInfoDao;

    @Autowired
    private WareRoleDao wareRoleDao;

    @Autowired
    private CardInfoDao cardInfoDao;

    @Override
    public WareInfoEntity queryObject(Long id) {
        return wareInfoDao.queryObject(id);
    }

    @Override
    public WareInfoEntity queryObjectByWareNo(String wareNo) {
        return wareInfoDao.queryObjectByWareNo(wareNo);
    }

    @Override
    public List<WareInfoEntity> queryList(Map<String, Object> map) {
        return wareInfoDao.queryList(map);
    }

    @Override
    public List<WareInfoVO> queryListAvailable(Map<String, Object> map) {
        return wareInfoDao.queryListAvailable(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return wareInfoDao.queryTotal(map);
    }

    @Override
    public void save(WareInfoEntity wareInfo) {
        wareInfoDao.save(wareInfo);

        saveWareRoles(wareInfo);
    }

    @Override
    public void update(WareInfoEntity wareInfo) {
        wareRoleDao.deleteByWareId(wareInfo.getId());
        saveWareRoles(wareInfo);
        wareInfoDao.update(wareInfo);
    }

    private void saveWareRoles(WareInfoEntity wareInfo) {
        List<Long> roleIds = wareInfo.getRoleIdList();
        if (roleIds == null || roleIds.size() == 0) {
            return;
        }
        for (Long roleId : roleIds) {
            WareRoleEntity wareRoleEntity = new WareRoleEntity();
            wareRoleEntity.setRoleId(roleId);
            wareRoleEntity.setWareInfoId(wareInfo.getId());
            wareRoleDao.save(wareRoleEntity);
        }
    }

    @Override
    public void delete(Long id) {
        wareInfoDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        wareInfoDao.deleteBatch(ids);
    }

    @Override
    public void offShelves(Long[] ids) {
        wareInfoDao.offShelves(ids);
    }

    @Override
    public List<Long> queryRoleIdList(Long id) {
        return wareRoleDao.queryRoleIdList(id);
    }

    @Override
    public WareInfoVO queryObjectAvailable(String wareNo) {
        return wareInfoDao.queryObjectAvailable(wareNo);
    }

    @Override
    public void shelves(Map<String, Object> map) {
        wareInfoDao.shelves(map);
    }

    /**
     * 获取卡密，可以优化成消息模式
     *
     * @param entity
     *
     * @return
     *
     * @throws IOException
     */
    @Override
    public String getPasscode(MsgPushEntity entity, Map<String, Object> orderInfo, List<Map<String, Object>> orders) {

        logger.debug("开始卡密处理。");
        String cardInfoString = "";

        for (Map<String, Object> order : orders) {
            // 查询商品是否卡密商品，获取商品编号去查找
            String outerItemId = (String) order.get("outer_item_id");
            String title = (String) order.get("title");

            //对应商品
            WareInfoEntity wareInfoEntity = wareInfoDao.queryObjectByWareNo(outerItemId);
            if (wareInfoEntity == null) {
                logger.debug("找不到对应商品，不需要处理");
                continue;
            }

            //卡密类型商品，开始查找对应卡密
            Integer quantity = Integer.parseInt(String.valueOf(order.get("num")));
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("wareNo", outerItemId);
            queryMap.put("num", quantity);
            queryMap.put("status", 1);
            List<CardInfoEntity> cardInfoLists = cardInfoDao.queryListNum(queryMap);
            if (cardInfoLists == null || cardInfoLists.size() < quantity) {
                logger.error("库存不足");
                return "";
            }
            cardInfoString += title + "卡密:";
            //卡密类型充值，获取卡密串
            for (int i = 0; i < cardInfoLists.size(); i++) {
                CardInfoEntity cardInfo = cardInfoLists.get(i);
                //将卡密状态改为2：已使用，同时存入代理商订单编号
                cardInfo.setRechargeTime(new Date());
                cardInfo.setStatus(2);
                cardInfo.setAgentOrderNo("yz" + orderInfo.get("tid"));
                cardInfoDao.update(cardInfo);
                cardInfoString += ((i + 1) + "、" + cardInfo.getPassword() + "，");
            }
            cardInfoString = cardInfoString.substring(0, cardInfoString.length() - 1) + "。";

            //根据商品编号查询商品库存
            WareInfoVO wareInfoVO = wareInfoDao.queryObjectAvailable(outerItemId);
            //库存为0，设置商品不可售
            if (wareInfoVO.getAvailable() == 0) {
                wareInfoEntity.setStatus(2);
                wareInfoDao.update(wareInfoEntity);
            }
        }
        return cardInfoString;
    }

}
