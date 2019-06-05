package com.funny.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author liyanjun
 */
@Component
@ConditionalOnProperty("optional.a.enable")
public class AConfig {
    @Value("${optional.a.userId}")
    private String userId;
    @Value("${optional.a.name}")
    private String name;
    @Value("${optional.a.key}")
    private String key;
    @Value("${optional.a.appKey}")
    private String appKey;
    @Value("${optional.a.appSecret}")
    private String appSecret;
    @Value("${optional.a.notifyUrl}")
    private String notifyUrl;
    @Value("${optional.a.url}")
    private String url;
    @Value("${optional.a.fuluCheckUrl}")
    private String checkUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCheckUrl() {
        return checkUrl;
    }

    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
