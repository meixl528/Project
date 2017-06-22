package com.ssm.interfaces.webservice.service.impl;

import javax.jws.WebService;

import com.ssm.interfaces.webservice.service.HelloWordService;

@WebService(endpointInterface = "com.ssm.interfaces.webservice.service.HelloWordService",serviceName="HelloWord")
public class HelloWordServiceImpl implements HelloWordService{

	@Override
	public String say() {
		System.out.println("aa = aa");
		return "{\"name\":\"bb\"}";
	}

}
