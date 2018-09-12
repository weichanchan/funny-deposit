package com.funny.admin.agent.entity;

import java.io.Serializable;


/**
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-11 23:07:34
 */
public class WareRoleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    //
    private Long id;
    //
    private Long roleId;
    //
    private Long wareInfoId;

    /**
     * 设置：
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置：
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * 获取：
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * 设置：
     */
    public void setWareInfoId(Long wareInfoId) {
        this.wareInfoId = wareInfoId;
    }

    /**
     * 获取：
     */
    public Long getWareInfoId() {
        return wareInfoId;
    }
}
