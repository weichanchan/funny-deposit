package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;



/**
 * 
 * 
 * @author weicc
 * @email 
 * @date 2019-06-12 10:52:06
 */
public class WareFuluRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
    /**
     * 设置：
     */
	private Long id;
    /**
     * 设置：
     */
	private Long roleId;
    /**
     * 设置：
     */
	private Long wareFuluInfoId;

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
	public void setWareFuluInfoId(Long wareFuluInfoId) {
		this.wareFuluInfoId = wareFuluInfoId;
	}
	/**
	 * 获取：
	 */
	public Long getWareFuluInfoId() {
		return wareFuluInfoId;
	}
}
