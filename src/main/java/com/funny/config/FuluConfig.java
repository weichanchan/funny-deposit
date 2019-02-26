package com.funny.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author liyanjun
 */
@Component
@ConditionalOnProperty("optional.fulu.enable")
public class FuluConfig {
    @Value("${optional.fulu.no}")
    private String no;
    @Value("${optional.fulu.name}")
    private String name;
    @Value("${optional.fulu.key}")
    private String key;
    @Value("${optional.fulu.notifyUrl}")
    private String notifyUrl;
    @Value("${optional.fulu.url}")
    private String url;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
