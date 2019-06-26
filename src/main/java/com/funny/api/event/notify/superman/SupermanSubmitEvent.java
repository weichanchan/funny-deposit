package com.funny.api.event.notify.superman;

import org.springframework.context.ApplicationEvent;

public class SupermanSubmitEvent extends ApplicationEvent {


    public SupermanSubmitEvent(Object source) {
        super(source);
    }

}
