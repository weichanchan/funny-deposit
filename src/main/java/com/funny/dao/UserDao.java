package com.funny.dao;

import com.funny.entity.UserEntity;

/**
 * 用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-23 15:22:06
 */
public interface UserDao extends BaseDao<UserEntity> {

    UserEntity queryByMobile(String mobile);
}
