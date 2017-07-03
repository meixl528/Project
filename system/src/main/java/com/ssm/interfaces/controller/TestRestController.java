package com.ssm.interfaces.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.interfaces.dto.InterfaceResponce;
import com.ssm.interfaces.service.IRestService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

//import freemarker.template.Configuration;

@Controller
public class TestRestController extends BaseController{
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IRestService restService;

	@RequestMapping(value = "/sys/interface/testRest")
    @ResponseBody
    public ResponseData testRest(HttpServletRequest request) throws Exception {
		//Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_21);
		
		String interfaceUrl = "http://192.168.10.27:8081/system/ws/helloWord2/rest/json";
		
		Map<String,Object> map = new HashMap<>();
		map.put("data", "测试");
		map.put("data2", "测试2");
		
		String jsonDatas = objectMapper.writeValueAsString(map);
		String loginName = "meixl";
		String loginPass = "meixl";
		
		InterfaceResponce<?> responce = restService.doProgress(interfaceUrl, jsonDatas, loginName, loginPass);
		
		ResponseData resp = new ResponseData(true);
		if(!responce.getStatusCode().equals(InterfaceResponce.statusCodeSuccess)){
			resp.setSuccess(false);
		}
		return resp;
	}
	
}
