package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;



/**
 * 卡门平台下单通讯记录
 * 
 * @author liyanjun
 * @date 2019-02-25 10:45:41
 */
public class OrderRequestRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
    /**
     * 设置：主键 ID
     */
	private Integer id;
    /**
     * 设置：请求参数
     */
	private String requst;
    /**
     * 设置：响应参数
     */
	private String response;
    /**
     * 设置：请求地址
     */
	private String url;
    /**
     * 设置：异常信息
     */
	private String exception;
    /**
     * 设置：订单编号
     */
	private String orderNo;
    /**
     * 设置：请求时间
     */
	private Date createTime;
    /**
     * 设置：响应时间
     */
	private Date responseTime;

	/**
	 * 设置：主键 ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：主键 ID
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：请求参数
	 */
	public void setRequst(String requst) {
		this.requst = requst;
	}
	/**
	 * 获取：请求参数
	 */
	public String getRequst() {
		return requst;
	}
	/**
	 * 设置：响应参数
	 */
	public void setResponse(String response) {
		this.response = response;
	}
	/**
	 * 获取：响应参数
	 */
	public String getResponse() {
		return response;
	}
	/**
	 * 设置：请求地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 获取：请求地址
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 设置：异常信息
	 */
	public void setException(String exception) {
		this.exception = exception;
	}
	/**
	 * 获取：异常信息
	 */
	public String getException() {
		return exception;
	}
	/**
	 * 设置：订单编号
	 */
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	/**
	 * 获取：订单编号
	 */
	public String getOrderNo() {
		return orderNo;
	}
	/**
	 * 设置：请求时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：请求时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * 设置：响应时间
	 */
	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}
	/**
	 * 获取：响应时间
	 */
	public Date getResponseTime() {
		return responseTime;
	}
}
