package com.funny.config;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author liyanjun
 */
@Component
@ConditionalOnProperty("optional.fulu.enable")
public class FuluConfig {
    @Value("${optional.fulu.userId}")
    private String userId;
    @Value("${optional.fulu.name}")
    private String name;
    @Value("${optional.fulu.key}")
    private String key;
    @Value("${optional.fulu.appKey}")
    private String appKey;
    @Value("${optional.fulu.appSecret}")
    private String appSecret;
    @Value("${optional.fulu.notifyUrl}")
    private String notifyUrl;
    @Value("${optional.fulu.url}")
    private String url;
    @Value("${optional.fulu.kamiUrl}")
    private String kamiUrl;
    @Value("${optional.fulu.feiKamiUrl}")
    private String feiKamiUrl;
    @Value("${optional.fulu.fuluCheckUrl}")
    private String fuluCheckUrl;
    @Value("${optional.fulu.huge}")
    private Integer huge;

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

    public Integer getHuge() {
        return huge;
    }

    public void setHuge(Integer huge) {
        this.huge = huge;
    }

    public String getKamiUrl() {
        return kamiUrl;
    }

    public void setKamiUrl(String kamiUrl) {
        this.kamiUrl = kamiUrl;
    }

    public String getFeiKamiUrl() {
        return feiKamiUrl;
    }

    public void setFeiKamiUrl(String feiKamiUrl) {
        this.feiKamiUrl = feiKamiUrl;
    }

    public String getFuluCheckUrl() {
        return fuluCheckUrl;
    }

    public void setFuluCheckUrl(String fuluCheckUrl) {
        this.fuluCheckUrl = fuluCheckUrl;
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
