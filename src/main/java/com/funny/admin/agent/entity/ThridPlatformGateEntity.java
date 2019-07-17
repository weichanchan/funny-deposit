package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * @author weicc
 * @email
 * @date 2019-07-10 16:39:52
 */
public class ThridPlatformGateEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int STATUS_PASS = 0;
    public static final int STATUS_CLOSE = 1;

    /**
     * 设置：
     */
    private Integer id;
    /**
     * 设置：平台名称
     */
    private String name;
    /**
     * 设置：状态，开启：0，关闭：1
     */
    private Integer status;
    /**
     * 设置：状态，开启：0，关闭：1
     */
    private Integer checkStatus;

    /**
     * 设置：
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取：
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置：平台名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取：平台名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置：状态，开启：0，关闭：1
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取：状态，开启：0，关闭：1
     */
    public Integer getStatus() {
        return status;
    }

    public Integer getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Integer checkStatus) {
        this.checkStatus = checkStatus;
    }
}
