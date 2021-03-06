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

import com.ssm.account.dto.User;
import com.ssm.core.request.IRequest;
import com.ssm.interfaces.dto.InterfaceResponce;
import com.ssm.interfaces.service.ISoapService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;
/**
 * @description 
 * HTTP协议本身是一种面向资源的应用层协议，但对HTTP协议的使用实际上存在着两种不同的方式：
 * 一种是RESTful的，它把HTTP当成应用层协议，比较忠实地遵守了HTTP协议的各种规定；
 * 另一种是SOA的，它并没有完全把HTTP当成应用层协议，而是把HTTP协议作为了传输层协议，然后在HTTP之上建立了自己的应用层协议。
 * 幂等性并不属于特定的协议，它是分布式系统的一种特性；
 * 所以，不论是SOA还是RESTful的Web API设计都应该考虑幂等性
 */
@Controller
public class TestSoapController extends BaseController{
	
	@Autowired
	private ISoapService soapService;
	
	@RequestMapping(value = "/sys/interface/testSoap")
    @ResponseBody
    public ResponseData testSoap(HttpServletRequest request,String interfaceUrl) throws Exception {
		IRequest irequest = createRequestContext(request);
		
		if(StringUtils.isEmpty(interfaceUrl)){
		    interfaceUrl = "http://localhost:8081/system/ws/helloWord/";
		}
		//传对象,直接template.process("对象map", writer);
		/*Map<String,Object> element = new HashMap<>();
		element.put("userName", "meixl");
		element.put("phone", "15021625324");*/
		
		//传list
		Map<String, Object> mapDatas = new HashMap<>();
		List<User> l = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			User user = new User();
			user.setUserName("meixl"+i);
			user.setPhone("15021625324"+i);
			l.add(user);
		}
		mapDatas.put("elements", l);
		
		//String sayDataTemplate = "Test_Interface_SayData"; //dto
		String sayTemplate = "Test_Interface_Say"; // list
		
		String loginName = "meixl";
		String loginPass = "meixl";
		
		InterfaceResponce<?> resp = soapService.doProgress(irequest,interfaceUrl, mapDatas, sayTemplate, loginName, loginPass);
		ResponseData response = new ResponseData(true);
        if(!resp.getStatusCode().equals(InterfaceResponce.statusCodeSuccess)){
        	response.setSuccess(false);
        }
        if(resp.getPojo()!=null){
			List<Object> list = new ArrayList<>();
			list.add(resp.getPojo());
			response.setRows(list);
		}
		if(resp.getRows()!=null && !resp.getRows().isEmpty()){
			response.setRows(resp.getRows());
		}
        return response;
    }
	
}
