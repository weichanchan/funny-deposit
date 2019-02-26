package com.funny.api.event.notify;

import org.springframework.context.ApplicationEvent;

public class YouzanRefundEvent extends ApplicationEvent {

    /**
     * 退款原因
     */
    private String reason;

    public YouzanRefundEvent(Object source, String reason) {
        super(source);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
