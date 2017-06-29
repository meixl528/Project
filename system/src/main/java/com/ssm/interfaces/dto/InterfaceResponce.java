package com.ssm.interfaces.dto;

import java.util.ArrayList;
import java.util.List;
/**
 * @name        InterfaceResponce
 * @description 接口返回实体
 * @author      meixl
 * @date        2017年6月27日下午1:17:12
 * @version
 */
public class InterfaceResponce<T> {
	
	static final String statusCodeSuccess = "S";
	static final String statusCodeError = "E";
	public InterfaceResponce() {}
	
	public InterfaceResponce(String statusCode) {
		this.statusCode = statusCode;
	}

	public InterfaceResponce(T pojo) {
		this.pojo = pojo;
		this.setStatusCode(statusCodeSuccess);
	}

	public InterfaceResponce(List<?> rows) {
		this.rows = rows;
		this.setStatusCode(statusCodeSuccess);
	}

	private String statusCode;
	
	private String message = "";
	
	private List<?> rows = new ArrayList<>();
	
	private T pojo;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}

	public T getPojo() {
		return pojo;
	}

	public void setPojo(T pojo) {
		this.pojo = pojo;
	}
	
}
