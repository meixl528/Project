package com.ssm.interfaces.webservice.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import com.ssm.interfaces.dto.Data;
import com.ssm.interfaces.dto.InterfaceRequest;
import com.ssm.interfaces.dto.InterfaceResponce;
import com.ssm.interfaces.webservice.HelloWordService;

@WebService(endpointInterface = "com.ssm.interfaces.webservice.HelloWordService",serviceName="HelloWords")
public class HelloWordServiceImpl implements HelloWordService{

	@Override
	public InterfaceResponce<Data> say(InterfaceRequest request) {
		List<Data> list  = new ArrayList<>();
		for (int i = 0; i < request.getElementsList().size(); i++) {
			System.out.println("获取到soap请求报文参数  d = "+ request.getElementsList().get(i).toString());
			
			Data data = new Data();
			data.setUserName("t_"+request.getElementsList().get(i).getUserName());
			data.setPhone("t_"+request.getElementsList().get(i).getPhone());
			list.add(data);
		}
		return new InterfaceResponce<Data>(list);
	}
	
	
	@Override
	public InterfaceResponce<Data> sayData(Data da) {
		System.out.println("获取到soap请求报文参数  data = "+da.toString());
		
		Data data = new Data();
		data.setUserName("data_"+da.getUserName());
		data.setPhone("data_"+da.getPhone());
		return new InterfaceResponce<Data>(data);
	}
	
}
