package com.funny.admin.agent.service;

import com.funny.admin.agent.entity.CardInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * 卡密信息表
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-07 13:33:51
 */
public interface CardInfoService {
	
	CardInfoEntity queryObject(Long id);

	List<CardInfoEntity> queryList(Map<String, Object> map);

    List<CardInfoEntity> queryListNum(Map<String, Object> map);

    List<CardInfoEntity> queryListByWareNo(Map<String, Object> map);

	List<CardInfoEntity> queryListByAgentOrderNo(String agentOrderNo);

	int queryTotal(Map<String, Object> map);

	int queryTotalNum(Map<String, Object> map);

	int queryTotalByWareNo(Map<String, Object> map);

	void save(CardInfoEntity cardInfo);
	
	void update(CardInfoEntity cardInfo);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);

    List<CardInfoEntity> queryListExisted(Map<String, Object> map);
}
