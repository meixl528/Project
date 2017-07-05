package com.ssm.interfaces.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.core.request.IRequest;
import com.ssm.interfaces.dto.InterfaceLog;
import com.ssm.interfaces.dto.InterfaceResponce;
import com.ssm.interfaces.service.IInetfaceLogService;
import com.ssm.interfaces.service.IRestService;
import com.ssm.util.HttpUtil;
import com.ssm.util.HttpUtil.HttpMethod;

/**
 * @name        RestServiceImpl
 * @description rest接口
 * @author      meixl
 * @date        2017年7月3日下午3:11:05
 * @version
 */
@Service
public class RestServiceImpl implements IRestService{
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private IMessageSender messageSender;
	@Autowired
	private IInetfaceLogService inetfaceLogService;

	@Override
	public InterfaceResponce<?> doProgress(IRequest iRequest,String interfaceUrl,String jsonDatas,String loginName,String loginPass) throws Exception {
		
		Map<String, String> headArg = new HashMap<String, String>();
		headArg.put("Content-Type", "application/json;charset=UTF-8");
		headArg.put("name", loginName);
		headArg.put("pass", loginPass);
		headArg.put("User-Agent", "SSM");
		
		// "http://192.168.10.27:8081/system/ws/helloWord2/rest/json"
		Map<String, Object> res = HttpUtil.net(interfaceUrl, jsonDatas, HttpMethod.POST, headArg);
		
		InterfaceResponce<?> responce = new InterfaceResponce<>();
		try {
			String result = (String)res.get("result");
			System.out.println("result String :");
			System.out.println(result);
			responce = objectMapper.readValue(result, responce.getClass());
			System.out.println("rest responce = "+ responce);
		} catch (IOException e) {
			throw new IOException(e.getMessage(), e);
		}
		
		try {
			//消息队列
			messageSender.sendQueue(res, "test.rest.queue");
		} finally{
			// write log 接口调用日志记录
			inetfaceLogService.insertSelective(iRequest, new InterfaceLog(interfaceUrl, null, responce.getStatusCode(), objectMapper.writeValueAsString(responce.getPojo())));
		}
		return responce;
	}

}
