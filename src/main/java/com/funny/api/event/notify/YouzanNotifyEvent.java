package com.funny.api.event.notify;

import org.springframework.context.ApplicationEvent;

public class YouzanNotifyEvent extends ApplicationEvent {


    public YouzanNotifyEvent(Object source) {
        super(source);
    }

    public YouzanNotifyEvent(Object source, String tid, String cardInfoString) {
        super(source);
    }

}
