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
    @Value("${optional.a.mctNo}")
    private String mctNo;
    @Value("${optional.a.name}")
    private String name;
    @Value("${optional.a.appKey}")
    private String appKey;
    @Value("${optional.a.url}")
    private String url;
    @Value("${optional.a.checkUrl}")
    private String checkUrl;
    @Value("${optional.a.clientKey}")
    private String clientKey;

    public String getMctNo() {
        return mctNo;
    }

    public void setMctNo(String mctNo) {
        this.mctNo = mctNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }
}
