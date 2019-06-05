package com.funny.api.event.notify.a;

import org.springframework.context.ApplicationEvent;

public class ACheckEvent extends ApplicationEvent {


    public ACheckEvent(Object source) {
        super(source);
    }

}
