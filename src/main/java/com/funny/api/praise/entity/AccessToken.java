package com.funny.api.praise.entity;

/**
 * 有赞TOKEN
 *
 * @author liyanjun
 */

public class AccessToken {

    /**
     * token 串
     */
    private String accessToken;
    /**
     * 过期时间
     */
    private Long expireTime;

    public AccessToken(String accessToken, Long expireTime) {
        this.accessToken = accessToken;
        this.expireTime = expireTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isExpire(){
        return System.currentTimeMillis() > expireTime;
    }
}
