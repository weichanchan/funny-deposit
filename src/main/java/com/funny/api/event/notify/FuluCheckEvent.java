package com.funny.api.event.notify;

import org.springframework.context.ApplicationEvent;

public class FuluCheckEvent extends ApplicationEvent {


    public FuluCheckEvent(Object source) {
        super(source);
    }

}
