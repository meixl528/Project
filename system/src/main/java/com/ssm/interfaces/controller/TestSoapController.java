package com.ssm.interfaces.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.account.dto.User;
import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.fnd.service.IStyleTemplateService;
import com.ssm.interfaces.dto.InterfaceResponce;
import com.ssm.interfaces.service.ISoapService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;
@Controller
public class TestSoapController extends BaseController{
	
	@Autowired
	private IStyleTemplateService styleTemplateService;
	
	@Autowired
	private IMessageSender messageSender;
	
	@Autowired
	private ISoapService soapService;
	
	@RequestMapping(value = "/sys/interface/testSoap")
    @ResponseBody
    public ResponseData testSoap(HttpServletRequest request) throws Exception {
		
		String interfaceUrl = "http://192.168.10.27:8081/system/ws/helloWord/";
		
		//传对象,直接template.process("对象map", writer);
		/*Map<String,Object> element = new HashMap<>();
		element.put("userName", "meixl");
		element.put("phone", "15021625324");*/
		
		//传list
		Map<String,List<User>> mapDatas = new HashMap<>();
		List<User> list = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			User user = new User();
			user.setUserName("meixl"+i);
			user.setPhone("15021625324"+i);
			list.add(user);
		}
		mapDatas.put("elements", list);
		
		String sayDataTemplate = "Test_Interface_SayData"; //dto
		String sayTemplate = "Test_Interface_Say"; // list
		
		String loginName = "meixl";
		String loginPass = "meixl";
		
		InterfaceResponce<?> resp = soapService.doProgress(interfaceUrl, mapDatas, sayTemplate, loginName, loginPass);
        
        return new ResponseData(true);
    }
	
}
