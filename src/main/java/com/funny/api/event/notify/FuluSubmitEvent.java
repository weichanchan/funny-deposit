package com.funny.api.event.notify;

import org.springframework.context.ApplicationEvent;

public class FuluSubmitEvent extends ApplicationEvent {


    public FuluSubmitEvent(Object source) {
        super(source);
    }

}
