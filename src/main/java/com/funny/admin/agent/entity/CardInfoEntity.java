package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 卡密信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-07 13:33:51
 */
public class CardInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Long id;
    //账号
    private String accountNo;
    //密码/激活码
    private String password;
    //关联商品编号
    private String wareNo;
    //关联订单编号
    private String agentOrderNo;
    //状态
    private Integer status;
    //有效期
    private Date expiryDate;
    //充值使用时间
    private Date rechargeTime;


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
     * 设置：账号
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    /**
     * 获取：账号
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * 设置：密码/激活码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取：密码/激活码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置：关联商品编号
     */
    public void setWareNo(String wareNo) {
        this.wareNo = wareNo;
    }

    /**
     * 获取：关联商品编号
     */
    public String getWareNo() {
        return wareNo;
    }

    /**
     * 设置：关联订单编号
     */
    public void setAgentOrderNo(String agentOrderNo) {
        this.agentOrderNo = agentOrderNo;
    }

    /**
     * 获取：关联订单编号
     */
    public String getAgentOrderNo() {
        return agentOrderNo;
    }

    /**
     * 设置：有效期
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * 获取：有效期
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }
}
