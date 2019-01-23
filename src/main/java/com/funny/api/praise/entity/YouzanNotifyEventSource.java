package com.funny.api.praise.entity;

/**
 * @author liyanjun
 */

public class YouzanNotifyEventSource {

    private String tid;
    private String cardInfoString;

    public YouzanNotifyEventSource(String tid, String cardInfoString) {
        this.tid = tid;
        this.cardInfoString = cardInfoString;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getCardInfoString() {
        return cardInfoString;
    }

    public void setCardInfoString(String cardInfoString) {
        this.cardInfoString = cardInfoString;
    }
}
