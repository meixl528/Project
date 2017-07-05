package com.ssm.interfaces.service;

import java.util.Map;

import com.ssm.core.request.IRequest;
import com.ssm.interfaces.dto.InterfaceResponce;

public interface ISoapService {
	
	/**
	 * soap interface
	 * @param interfaceUrl  接口url
	 * @param mapDatas      map参数
	 * @param template      模板代码
	 * @param loginName     登录名
	 * @param loginPass     登录密码
	 */
	InterfaceResponce<?> doProgress(IRequest irequest,String interfaceUrl, Map<String,Object> mapDatas,String template, String loginName, String loginPass) throws Exception;

}
