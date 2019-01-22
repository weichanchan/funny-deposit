package com.funny.api.praise.entity;

/**
 * 消息接收类
 * <p>
 * 文档：https://open.youzan.com/v3/apicenter/doc-api-main/1/1/4404
 */
public class MsgPushEntity {
    private String msg;
    private int sendCount;
    /**
     * 默认0 : appid  1 :client
     */
    private int mode;
    private String app_id;
    private String client_id;
    private Long version;
    private String type;
    private String id;
    private String sign;
    private Integer kdt_id;
    private boolean test = false;
    private String status;
    private String kdt_name;
    private String msg_id;

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Integer getKdt_id() {
        return kdt_id;
    }

    public void setKdt_id(Integer kdt_id) {
        this.kdt_id = kdt_id;
    }

    public String getKdt_name() {
        return kdt_name;
    }

    public void setKdt_name(String kdt_name) {
        this.kdt_name = kdt_name;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

}