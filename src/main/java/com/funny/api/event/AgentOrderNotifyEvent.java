package com.funny.api.event;

import org.springframework.context.ApplicationEvent;

/**
 * 充值通知事件
 */
public class AgentOrderNotifyEvent extends ApplicationEvent {
    public AgentOrderNotifyEvent(Object source) {
        super(source);
    }
}
