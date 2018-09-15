package com.funny.admin.agent.entity;

import java.util.List;

public class WareInfoVO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 商品编号
     */
    private String wareNo;
    /**
     * 代理商价格
     */
    private Long agentPrice;
    /**
     * 充值类型，1：直充类型:；2：卡密类型
     */
    private Integer type;
    /**
     * 商品状态，1：可售；2：不可售
     */
    private Integer status;
    /**
     * 角色ID列表
     */
    private List<Long> roleIdList;
    /**
     * 关联的代理商id
     */
    private String agentId;
    private Integer available;
    private Integer cardInfoTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWareNo() {
        return wareNo;
    }

    public void setWareNo(String wareNo) {
        this.wareNo = wareNo;
    }

    public Long getAgentPrice() {
        return agentPrice;
    }

    public void setAgentPrice(Long agentPrice) {
        this.agentPrice = agentPrice;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Long> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Long> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getCardInfoTotal() {
        return cardInfoTotal;
    }

    public void setCardInfoTotal(Integer cardInfoTotal) {
        this.cardInfoTotal = cardInfoTotal;
    }
}
