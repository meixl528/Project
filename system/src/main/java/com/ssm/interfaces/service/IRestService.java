package com.ssm.interfaces.service;

import com.ssm.core.request.IRequest;
import com.ssm.interfaces.dto.InterfaceResponce;

public interface IRestService {
	
	InterfaceResponce<?> doProgress(IRequest iRequest,String interfaceUrl, String jsonDatas, String loginName, String loginPass) throws Exception;

}
