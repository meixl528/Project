package com.ssm.interfaces.webservice.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ssm.interfaces.dto.Data;
import com.ssm.interfaces.dto.InterfaceResponce;
import com.ssm.interfaces.webservice.HelloWordService3;

public class HelloWordServiceImpl3 implements HelloWordService3{
	
	static Logger logger = LoggerFactory.getLogger(HelloWordService3.class);

	@Override
	public InterfaceResponce<Data> rest(Data da) {
		logger.debug(this.getClass().getName());
		System.out.println("获取到rest请求报文参数  data = "+da.toString());
		
		return new InterfaceResponce<Data>(da);
	}
	
}
