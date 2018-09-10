package com.funny.admin.agent.entity;

import java.io.Serializable;
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
    private String jdOrderNo;
    //订单类型，1：普通
    private Integer type;
    //清算时间
    private String finTime;
    //回调通知地址
    private String notifyUrl;
    //充值号码
    private String rechargeNum;
    //数量
    private Integer quantity;
    //商品编码
    private String wareNo;
    //成本价
    private Long costPrice;
    //特殊属性
    private String features;
    //订单状态，1：新创建；2：处理中；3：已处理
    private Integer status;
    //充值状态，1：充值成功；2：充值失败；3：充值中
    private Integer rechargeStatus;
    //卡密信息
    private Integer cardInfo;
    //订单创建时间
    private Date createTime;
    private Date handleTime;
    private String sign;
    private String signType;
    private String timestamp;
    private String version;

    public AgentOrderEntity() {
    }

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
    public void setJdOrderNo(String jdOrderNo) {
        this.jdOrderNo = jdOrderNo;
    }

    /**
     * 获取：京东订单号
     */
    public String getJdOrderNo() {
        return jdOrderNo;
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
    public void setFinTime(String finTime) {
        this.finTime = finTime;
    }

    /**
     * 获取：清算时间
     */
    public String getFinTime() {
        return finTime;
    }

    /**
     * 设置：回调通知地址
     */
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    /**
     * 获取：回调通知地址
     */
    public String getNotifyUrl() {
        return notifyUrl;
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
    public void setWareNo(String wareNo) {
        this.wareNo = wareNo;
    }

    /**
     * 获取：商品编码
     */
    public String getWareNo() {
        return wareNo;
    }

    /**
     * 设置：成本价
     */
    public void setCostPrice(Long costPrice) {
        this.costPrice = costPrice;
    }

    /**
     * 获取：成本价
     */
    public Long getCostPrice() {
        return costPrice;
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
     * 设置：订单状态，1：新创建；2：处理中；3：已处理
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取：订单状态，1：新创建；2：处理中；3：已处理
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置：充值状态，1：充值成功；2：充值失败；3：充值中
     */
    public void setRechargeStatus(Integer rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }

    /**
     * 获取：充值状态，1：充值成功；2：充值失败；3：充值中
     */
    public Integer getRechargeStatus() {
        return rechargeStatus;
    }

    /**
     * 设置：卡密信息
     */
    public void setCardInfo(Integer cardInfo) {
        this.cardInfo = cardInfo;
    }

    /**
     * 获取：卡密信息
     */
    public Integer getCardInfo() {
        return cardInfo;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }
}
