package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.List;


/**
 * 商品信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-04 11:13:01
 */
public class WareInfoEntity implements Serializable {
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
     * 设置：充值类型，1：直充类型:；2：卡密类型
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取：充值类型，1：直充类型:；2：卡密类型
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置：商品状态，1：可售；2：不可售
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取：商品状态，1：可售；2：不可售
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置：关联的代理商id
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * 获取：关联的代理商id
     */
    public String getAgentId() {
        return agentId;
    }

    public List<Long> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Long> roleIdList) {
        this.roleIdList = roleIdList;
    }
}
