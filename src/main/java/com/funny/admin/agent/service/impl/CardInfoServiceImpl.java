package com.funny.admin.agent.service.impl;

import com.funny.admin.agent.dao.CardInfoDao;
import com.funny.admin.agent.entity.CardInfoEntity;
import com.funny.admin.agent.service.CardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;



@Service("cardInfoService")
public class CardInfoServiceImpl implements CardInfoService {
	@Autowired
	private CardInfoDao cardInfoDao;
	
	@Override
	public CardInfoEntity queryObject(Long id){
		return cardInfoDao.queryObject(id);
	}
	
	@Override
	public List<CardInfoEntity> queryList(Map<String, Object> map){
		return cardInfoDao.queryList(map);
	}

	@Override
	public List<CardInfoEntity> queryListNum(Map<String, Object> map) {
		return cardInfoDao.queryListNum(map);
	}

    @Override
    public List<CardInfoEntity> queryListByWareNo(Map<String, Object> map) {
        return cardInfoDao.queryListByWareNo(map);
    }

	@Override
	public List<CardInfoEntity> queryListByAgentOrderNo(String agentOrderNo) {
		return cardInfoDao.queryListByAgentOrderNo(agentOrderNo);
	}

	@Override
	public int queryTotal(Map<String, Object> map){
		return cardInfoDao.queryTotal(map);
	}

	@Override
	public int queryTotalNum(Map<String, Object> map) {
		return cardInfoDao.queryTotalNum(map);
	}

	@Override
	public int queryTotalByWareNo(Map<String, Object> map) {
		return cardInfoDao.queryTotalByWareNo(map);
	}

	@Override
	public void save(CardInfoEntity cardInfo){
		cardInfoDao.save(cardInfo);
	}
	
	@Override
	public void update(CardInfoEntity cardInfo){
		cardInfoDao.update(cardInfo);
	}
	
	@Override
	public void delete(Long id){
		cardInfoDao.delete(id);
	}
	
	@Override
	public void deleteBatch(Long[] ids){
		cardInfoDao.deleteBatch(ids);
	}

	@Override
	public List<CardInfoEntity> queryListExisted(Map<String, Object> map) {
		return cardInfoDao.queryListByPwds(map);
	}

}
