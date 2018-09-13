package com.funny.admin.agent.entity;

import java.io.Serializable;


/**
 * 代理商信息表
 *
 * @author weicc
 * @email sunlightcs@gmail.com
 * @date 2018-09-05 18:04:57
 */
public class AgentInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 代理商id
     */
    private String agentId;
    /**
     * 商家名
     */
    private String agentName;
    /**
     * 联系电话
     */
    private String agentPhone;

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
     * 设置：代理商id
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * 获取：代理商id
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * 设置：商家名
     */
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    /**
     * 获取：商家名
     */
    public String getAgentName() {
        return agentName;
    }

    /**
     * 设置：联系电话
     */
    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    /**
     * 获取：联系电话
     */
    public String getAgentPhone() {
        return agentPhone;
    }
}
