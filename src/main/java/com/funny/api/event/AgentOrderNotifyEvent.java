package com.funny.api.event;

import com.funny.admin.agent.entity.AgentOrderEntity;
import com.funny.admin.agent.entity.WareInfoEntity;
import org.springframework.context.ApplicationEvent;

/**
 * 充值通知事件
 */
public class AgentOrderNotifyEvent extends ApplicationEvent {
    /**
     * 商家不存在
     */
    public static final String JD00001 = "JDO_00001";
    /**
     * IP没有权限
     */
    public static final String JD00002 = "JDO_00002";
    /**
     * 参数错误
     */
    public static final String JD00003 = "JDO_00003";
    /**
     * 签名错误
     */
    public static final String JD00004 = "JDO_00004";
    /**
     * 对应状态不存在
     */
    public static final String JD00005 = "JDO_00005";
    /**
     * 此订单不存在
     */
    public static final String JD00006 = "JDO_00006";
    /**
     * 代理商ID不正确
     */
    public static final String JD00007 = "JDO_00007";
    /**
     * 京东订单状态不正确
     */
    public static final String JD00008 = "JDO_00008";
    /**
     * 订单处理失败
     */
    public static final String JD00009 = "JDO_00009";
    /**
     * 系统异常
     */
    public static final String JD00000 = "JDO_00000";
    /**
     * 此商品不可售
     */
    public static final String JDI00004 = "JDI_00004";

    private AgentOrderEntity agentOrderEntity;

    private String cardInfoString;

    private WareInfoEntity wareInfoEntity;

    private String jdReturnCode;

    public AgentOrderNotifyEvent(Object source, AgentOrderEntity agentOrderEntity, String cardInfoString, WareInfoEntity wareInfoEntity, String jdReturnCode) {
        super(source);
        this.agentOrderEntity = agentOrderEntity;
        this.cardInfoString = cardInfoString;
        this.wareInfoEntity = wareInfoEntity;
        this.jdReturnCode = jdReturnCode;
    }


    public AgentOrderEntity getAgentOrderEntity() {
        return agentOrderEntity;
    }

    public void setAgentOrderEntity(AgentOrderEntity agentOrderEntity) {
        this.agentOrderEntity = agentOrderEntity;
    }

    public String getCardInfoString() {
        return cardInfoString;
    }

    public void setCardInfoString(String cardInfoString) {
        this.cardInfoString = cardInfoString;
    }

    public WareInfoEntity getWareInfoEntity() {
        return wareInfoEntity;
    }

    public void setWareInfoEntity(WareInfoEntity wareInfoEntity) {
        this.wareInfoEntity = wareInfoEntity;
    }

    public String getJdReturnCode() {
        return jdReturnCode;
    }

    public void setJdReturnCode(String jdReturnCode) {
        this.jdReturnCode = jdReturnCode;
    }
}
