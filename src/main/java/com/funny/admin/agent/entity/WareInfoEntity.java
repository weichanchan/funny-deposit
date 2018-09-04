package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;



/**
 * 商品信息表
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-04 11:13:01
 */
public class WareInfoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//商品编号
	private String wareNo;
	//代理商价格
	private Long agentPrice;
	//充值类型，直充类型：1；卡密类型：2
	private Integer type;

	/**
	 * 设置：主键id
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：商品编号
	 */
	public void setWareNo(String wareNo) {
		this.wareNo = wareNo;
	}
	/**
	 * 获取：商品编号
	 */
	public String getWareNo() {
		return wareNo;
	}
	/**
	 * 设置：代理商价格
	 */
	public void setAgentPrice(Long agentPrice) {
		this.agentPrice = agentPrice;
	}
	/**
	 * 获取：代理商价格
	 */
	public Long getAgentPrice() {
		return agentPrice;
	}
	/**
	 * 设置：充值类型，直充类型：1；卡密类型：2
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：充值类型，直充类型：1；卡密类型：2
	 */
	public Integer getType() {
		return type;
	}
}
