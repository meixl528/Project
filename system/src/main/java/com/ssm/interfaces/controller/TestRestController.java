package com.ssm.interfaces.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.core.request.IRequest;
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
    public ResponseData testRest(HttpServletRequest request,String interfaceUrl) throws Exception {
		//Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_21);
		IRequest iRequest = createRequestContext(request);
		
		if(StringUtils.isEmpty(interfaceUrl)){
			interfaceUrl = "http://localhost:8081/system/ws/helloWord2/rest/json";
		}
		
		Map<String,Object> map = new HashMap<>();
		map.put("userName", "测试");
		map.put("phone", "测试2");
		
		String jsonDatas = objectMapper.writeValueAsString(map);
		String loginName = "meixl";
		String loginPass = "meixl";
		
		InterfaceResponce<?> resp = restService.doProgress(iRequest, interfaceUrl, jsonDatas, loginName, loginPass);
		
		ResponseData responce = new ResponseData(true);
		if(!resp.getStatusCode().equals(InterfaceResponce.statusCodeSuccess)){
			responce.setSuccess(false);
		}
		if(resp.getPojo()!=null){
			List<Object> list = new ArrayList<>();
			list.add(resp.getPojo());
			responce.setRows(list);
		}
		if(resp.getRows()!=null && !resp.getRows().isEmpty()){
			responce.setRows(resp.getRows());
		}
		return responce;
	}
	
}
