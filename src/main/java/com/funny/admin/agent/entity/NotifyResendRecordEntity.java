package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;



/**
 * 重发消息记录
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-16 22:53:41
 */
public class NotifyResendRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
    /**
     * 设置：主键 ID
     */
	private Long id;
    /**
     * 设置：通知地址参数
     */
	private String notifyUrl;
    /**
     * 设置：下一次发送时间
     */
	private Date nextTime;
    /**
     * 设置：已重复次数
     */
	private Integer count;
    /**
     * 设置：通知超限
     */
	private Date failTime;
    /**
     * 设置：创建时间
     */
	private Date creationTime;
    /**
     * 设置：订单 ID
     */
	private Long agentOrderId;

	/**
	 * 设置：主键 ID
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：主键 ID
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：通知地址参数
	 */
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	/**
	 * 获取：通知地址参数
	 */
	public String getNotifyUrl() {
		return notifyUrl;
	}
	/**
	 * 设置：下一次发送时间
	 */
	public void setNextTime(Date nextTime) {
		this.nextTime = nextTime;
	}
	/**
	 * 获取：下一次发送时间
	 */
	public Date getNextTime() {
		return nextTime;
	}
	/**
	 * 设置：已重复次数
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
	/**
	 * 获取：已重复次数
	 */
	public Integer getCount() {
		return count;
	}
	/**
	 * 设置：通知超限
	 */
	public void setFailTime(Date failTime) {
		this.failTime = failTime;
	}
	/**
	 * 获取：通知超限
	 */
	public Date getFailTime() {
		return failTime;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	/**
	 * 获取：创建时间
	 */
	public Date getCreationTime() {
		return creationTime;
	}
	/**
	 * 设置：订单 ID
	 */
	public void setAgentOrderId(Long agentOrderId) {
		this.agentOrderId = agentOrderId;
	}
	/**
	 * 获取：订单 ID
	 */
	public Long getAgentOrderId() {
		return agentOrderId;
	}
}
