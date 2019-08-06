package com.funny.admin.agent.entity;

import java.io.Serializable;
import java.util.Date;



/**
 * 拼多多配置文件
 * 
 * @author weicc
 * @email 
 * @date 2019-07-26 10:28:38
 */
public class ThridPddConfigEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
    /**
     * 设置：组件 ID
     */
	private Integer id;
    /**
     * 设置：拼多多 client_id
     */
	private String clientId;
    /**
     * 设置：拼多多 client_secret
     */
	private String clientSecret;
    /**
     * 设置：拼多多 refresh_token
     */
	private String refreshToken;
    /**
     * 设置：拼多多 code
     */
	private String code;

	/**
	 * 设置：组件 ID
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：组件 ID
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：拼多多 client_id
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * 获取：拼多多 client_id
	 */
	public String getClientId() {
		return clientId;
	}
	/**
	 * 设置：拼多多 client_secret
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	/**
	 * 获取：拼多多 client_secret
	 */
	public String getClientSecret() {
		return clientSecret;
	}
	/**
	 * 设置：拼多多 refresh_token
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	/**
	 * 获取：拼多多 refresh_token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	/**
	 * 设置：拼多多 code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * 获取：拼多多 code
	 */
	public String getCode() {
		return code;
	}
}
