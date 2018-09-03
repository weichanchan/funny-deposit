package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;



/**
 * 代理商订单表
 * 
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-03 10:23:02
 */
public class AgentOrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//主键id
	private Long id;
	//代理商订单号
	private String agentOrderNo;
	//京东订单号
	private String jdOrderId;
	//订单类型，1：普通
	private Integer type;
	//清算时间
	private String fintime;
	//回调通知地址
	private String notifyurl;
	//充值号码
	private String rechargeNum;
	//数量
	private Integer quantity;
	//商品编码
	private String wareno;
	//成本价
	private Long costprice;
	//特殊属性
	private String features;
	//订单状态，1：充值成功；2：充值失败；3：充值中
	private Integer status;
	//订单创建时间
	private Date createTime;

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
	 * 设置：代理商订单号
	 */
	public void setAgentOrderNo(String agentOrderNo) {
		this.agentOrderNo = agentOrderNo;
	}
	/**
	 * 获取：代理商订单号
	 */
	public String getAgentOrderNo() {
		return agentOrderNo;
	}
	/**
	 * 设置：京东订单号
	 */
	public void setJdOrderId(String jdOrderId) {
		this.jdOrderId = jdOrderId;
	}
	/**
	 * 获取：京东订单号
	 */
	public String getJdOrderId() {
		return jdOrderId;
	}
	/**
	 * 设置：订单类型，1：普通
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 获取：订单类型，1：普通
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 设置：清算时间
	 */
	public void setFintime(String fintime) {
		this.fintime = fintime;
	}
	/**
	 * 获取：清算时间
	 */
	public String getFintime() {
		return fintime;
	}
	/**
	 * 设置：回调通知地址
	 */
	public void setNotifyurl(String notifyurl) {
		this.notifyurl = notifyurl;
	}
	/**
	 * 获取：回调通知地址
	 */
	public String getNotifyurl() {
		return notifyurl;
	}
	/**
	 * 设置：充值号码
	 */
	public void setRechargeNum(String rechargeNum) {
		this.rechargeNum = rechargeNum;
	}
	/**
	 * 获取：充值号码
	 */
	public String getRechargeNum() {
		return rechargeNum;
	}
	/**
	 * 设置：数量
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	/**
	 * 获取：数量
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * 设置：商品编码
	 */
	public void setWareno(String wareno) {
		this.wareno = wareno;
	}
	/**
	 * 获取：商品编码
	 */
	public String getWareno() {
		return wareno;
	}
	/**
	 * 设置：成本价
	 */
	public void setCostprice(Long costprice) {
		this.costprice = costprice;
	}
	/**
	 * 获取：成本价
	 */
	public Long getCostprice() {
		return costprice;
	}
	/**
	 * 设置：特殊属性
	 */
	public void setFeatures(String features) {
		this.features = features;
	}
	/**
	 * 获取：特殊属性
	 */
	public String getFeatures() {
		return features;
	}
	/**
	 * 设置：订单状态，1：充值成功；2：充值失败；3：充值中
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：订单状态，1：充值成功；2：充值失败；3：充值中
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：订单创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：订单创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}
}
