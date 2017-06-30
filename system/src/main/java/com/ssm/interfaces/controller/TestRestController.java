package com.ssm.interfaces.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.util.HttpUtil;
import com.ssm.util.HttpUtil.HttpMethod;

//import freemarker.template.Configuration;

@Controller
public class TestRestController extends BaseController{
	
	//@Autowired
	//private IStyleTemplateService styleTemplateService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IMessageSender messageSender;

	@RequestMapping(value = "/sys/interface/testRest")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseData testRest(HttpServletRequest request) throws Exception {
		//Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_21);
		
		Map<String,Object> element = new HashMap<>();
		element.put("userName", "meixl");
		element.put("phone", "15021625324");
		
		String JsonStr = objectMapper.writeValueAsString(element);
		
		/*String restData = "Test_Rest"; //dto
		
		//模板加载
		StyleTemplate styleTemplate = styleTemplateService.getStyleTemplate(restData);
		if(styleTemplate == null){
			throw new IllegalArgumentException("Invalid TemplateCode");
		}
		String templateString = styleTemplate.getTemplateContent();
		
		//渲染
		StringWriter writer = new StringWriter(1024);
		Template template = new Template(restData, new StringReader(templateString), freeMarkerConfig);
		// 绑定
		template.process(element, writer);
		String requestJson =writer.toString();*/
		
		
		Map<String, String> headArg = new HashMap<String, String>();
		headArg.put("Content-Type", "application/json;charset=UTF-8");
		headArg.put("name", "meixl");
		headArg.put("pass", "meixl");
		headArg.put("User-Agent", "HAP");
		
		Map<String, Object> res = HttpUtil.net("http://192.168.10.32:8081/system/ws/helloWord2/rest/json", JsonStr, HttpMethod.POST, headArg);
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		try {
			String result = (String)res.get("result");
			System.out.println("result String :");
			System.out.println(result);
			jsonMap = objectMapper.readValue(result, jsonMap.getClass());
			System.out.println("jsonMap = "+ jsonMap);
		} catch (IOException jacksonE) {
			throw new IOException(jacksonE.getMessage(), jacksonE);
		}
		
		//消息队列
		messageSender.sendQueue(res, "test.rest.queue");
		
		return new ResponseData(true);
	}
	
}
