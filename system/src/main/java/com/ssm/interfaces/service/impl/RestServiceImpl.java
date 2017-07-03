package com.ssm.interfaces.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.interfaces.dto.InterfaceResponce;
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

	@SuppressWarnings("unchecked")
	@Override
	public InterfaceResponce<Map<String, Object>> doProgress(String interfaceUrl,String jsonDatas,String loginName,String loginPass) throws Exception {
		
		Map<String, String> headArg = new HashMap<String, String>();
		headArg.put("Content-Type", "application/json;charset=UTF-8");
		headArg.put("name", loginName);
		headArg.put("pass", loginPass);
		headArg.put("User-Agent", "SSM");
		
		// "http://192.168.10.27:8081/system/ws/helloWord2/rest/json"
		Map<String, Object> res = HttpUtil.net(interfaceUrl, jsonDatas, HttpMethod.POST, headArg);
		
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		try {
			String result = (String)res.get("result");
			System.out.println("result String :");
			System.out.println(result);
			jsonMap = objectMapper.readValue(result, jsonMap.getClass());
			System.out.println("jsonMap = "+ jsonMap);
		} catch (IOException e) {
			throw new IOException(e.getMessage(), e);
		}
		
		try {
			//消息队列
			messageSender.sendQueue(res, "test.rest.queue");
		} finally{
			// write log
		}
		return new InterfaceResponce<>(jsonMap);
	}

}
