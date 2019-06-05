package com.funny.api.event.notify.a;

import org.springframework.context.ApplicationEvent;

public class ASubmitEvent extends ApplicationEvent {


    public ASubmitEvent(Object source) {
        super(source);
    }

}
