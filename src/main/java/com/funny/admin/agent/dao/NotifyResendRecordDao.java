package com.funny.admin.agent.dao;

import com.funny.admin.agent.entity.NotifyResendRecordEntity;
import com.funny.admin.system.dao.BaseDao;

import java.util.List;

/**
 * 重发消息记录
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-16 22:53:41
 */
public interface NotifyResendRecordDao extends BaseDao<NotifyResendRecordEntity> {

    List<NotifyResendRecordEntity> queryResendList();
}
