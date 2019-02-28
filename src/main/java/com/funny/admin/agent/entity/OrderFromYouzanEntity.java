package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 有赞已支付成功订单
 *
 * @author liyanjun
 * @date 2019-02-25 10:45:40
 */
public class OrderFromYouzanEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 1、充值成功
     */
    public static final int SUCCESS = 1;
    /**
     * 2、待充值
     */
    public static final int WAIT_PROCESS = 2;
    /**
     * 3、充值中
     */
    public static final int PROCESS = 3;
    /**
     * 4、退款成功
     */
    public static final int REFUND_SUCCESS = 4;
    /**
     * -1、失败，准备退款
     */
    public static final int FAIL = -1;
    /**
     * -2、异常，重试中
     */
    public static final int EXCEPTION = -2;
    /**
     * -3、退款失败
     */
    public static final int REFUND_FAIL = -3;
    /**
     * 设置：主键 ID
     */
    private Integer id;
    /**
     * 设置：用于跟外部系统对接的订单号
     */
    private String orderNo;
    /**
     * 设置：有赞订单号
     */
    private String youzanOrderId;
    /**
     * 设置：订单金额
     */
    private BigDecimal orderPrice;
    /**
     * 设置：有赞子订单号
     */
    private String subOrderId;
    /**
     * 设置：商品编号
     */
    private String wareNo;
    /**
     * 设置：1、成功。2、处理中。-1、失败。-2、异常
     */
    private Integer status;
    /**
     * 设置：购买商品的规格信息
     */
    private String formatInfo;
    /**
     * 设置：充值用户相关信息字段
     */
    private String rechargeInfo;
    /**
     * 设置：订单创建时间
     */
    private Date createTime;
    /**
     * 设置：开始去卡门平台充值时间
     */
    private Date beginRechargeTime;
    /**
     * 设置：最后一次去卡门平台请求充值时间
     */
    private Date lastRechargeTime;

    /**
     * 异常状况
     */
    private String exception;

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
     * 设置：用于跟外部系统对接的订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取：用于跟外部系统对接的订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置：有赞订单号
     */
    public void setYouzanOrderId(String youzanOrderId) {
        this.youzanOrderId = youzanOrderId;
    }

    /**
     * 获取：有赞订单号
     */
    public String getYouzanOrderId() {
        return youzanOrderId;
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
     * 设置：1、成功。2、处理中。-1、失败。-2、异常
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取：1、成功。2、处理中。-1、失败。-2、异常
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置：购买商品的规格信息
     */
    public void setFormatInfo(String formatInfo) {
        this.formatInfo = formatInfo;
    }

    /**
     * 获取：购买商品的规格信息
     */
    public String getFormatInfo() {
        return formatInfo;
    }

    /**
     * 设置：充值用户相关信息字段
     */
    public void setRechargeInfo(String rechargeInfo) {
        this.rechargeInfo = rechargeInfo;
    }

    /**
     * 获取：充值用户相关信息字段
     */
    public String getRechargeInfo() {
        return rechargeInfo;
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

    /**
     * 设置：开始去卡门平台充值时间
     */
    public void setBeginRechargeTime(Date beginRechargeTime) {
        this.beginRechargeTime = beginRechargeTime;
    }

    /**
     * 获取：开始去卡门平台充值时间
     */
    public Date getBeginRechargeTime() {
        return beginRechargeTime;
    }

    /**
     * 设置：最后一次去卡门平台请求充值时间
     */
    public void setLastRechargeTime(Date lastRechargeTime) {
        this.lastRechargeTime = lastRechargeTime;
    }

    /**
     * 获取：最后一次去卡门平台请求充值时间
     */
    public Date getLastRechargeTime() {
        return lastRechargeTime;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(String subOrderId) {
        this.subOrderId = subOrderId;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }
}
