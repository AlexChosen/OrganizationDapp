package com.pingan.chain.common;

import java.io.Serializable;
/**
 * 公共实体
 * @author song.zhang
 *
 * @param <T>
 */
public class ResponseMessage<T> implements Serializable {
	
	/**
	 * code码
	 */
	private String code;
	/**
	 * 描述
	 */
	private String message;
	/**
	 * 结果集
	 */
	private T data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	
}
