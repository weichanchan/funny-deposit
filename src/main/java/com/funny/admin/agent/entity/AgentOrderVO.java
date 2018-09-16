package com.funny.admin.agent.entity;

import java.util.Date;

public class AgentOrderVO {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 京东订单号
     */
    private String jdOrderNo;
    /**
     * 充值号码
     */
    private String rechargeNum;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 商品编码
     */
    private String wareNo;
    /**
     * 商品名称
     */
    private String wareName;
    /**
     * 成本价
     */
    private Long costPrice;
    /**
     * 订单状态，1:新创建；2：处理中；3：已处理
     */
    private Integer status;
    /**
     * 充值状态，0：未充值；1：充值成功；2：充值失败；3：充值中
     */
    private Integer rechargeStatus;

    /**
     * 处理时间
     */
    private Date handleTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJdOrderNo() {
        return jdOrderNo;
    }

    public void setJdOrderNo(String jdOrderNo) {
        this.jdOrderNo = jdOrderNo;
    }

    public String getRechargeNum() {
        return rechargeNum;
    }

    public void setRechargeNum(String rechargeNum) {
        this.rechargeNum = rechargeNum;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getWareNo() {
        return wareNo;
    }

    public void setWareNo(String wareNo) {
        this.wareNo = wareNo;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    public Long getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Long costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(Integer rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }
}
