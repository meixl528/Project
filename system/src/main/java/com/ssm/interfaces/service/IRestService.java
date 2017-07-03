package com.ssm.interfaces.service;

import com.ssm.interfaces.dto.InterfaceResponce;

public interface IRestService {
	
	InterfaceResponce<?> doProgress(String interfaceUrl, String jsonDatas, String loginName, String loginPass) throws Exception;

}
