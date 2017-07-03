package com.ssm.interfaces.service;

import java.util.Map;

import com.ssm.interfaces.dto.InterfaceResponce;

public interface ISoapService {
	
	InterfaceResponce<?> doProgress(String interfaceUrl, Map<String,? extends Object> mapDatas,String template, String loginName, String loginPass) throws Exception;

}
