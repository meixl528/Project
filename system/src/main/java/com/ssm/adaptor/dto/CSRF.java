package com.ssm.adaptor.dto;

import java.io.Serializable;

public class CSRF implements Serializable{
		private static final long serialVersionUID = 1L;
		private String parameterName;
		private String token;
    	public CSRF(){};
    	public CSRF(String parameterName,String token){
    		this.parameterName = parameterName;
    		this.token = token;
    	}
		public String getParameterName() {
			return parameterName;
		}
		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}
		
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		
		@Override
		public String toString() {
			return "CSRF [parameterName=" + parameterName + ", token=" + token + "]";
		}
		
		public String toJson() {
			return "{\"parameterName\"=\"" + parameterName + "\", \"token\"=\"" + token + "\"]";
		}
    }