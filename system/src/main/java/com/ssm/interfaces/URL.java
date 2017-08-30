package com.ssm.interfaces;

public enum URL {
	
	Test_Rest_Controller_URL("http://localhost:8081/system/ws/helloWord2/rest/json","Rest接口","Rest_Itf"),

	Test_Soap_Controller_URL("http://localhost:8081/system/ws/helloWord/","Soap接口","Soap_Itf");
	
	private String url,name,code;
	private URL(String url, String name, String code) {
		this.url = url;
		this.name = name;
		this.code = code;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
