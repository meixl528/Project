package com.ssm.interfaces.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ssm.account.dto.User;
import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.fnd.service.IStyleTemplateService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.util.XmlUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
@Controller
public class TestSoapController extends BaseController{
	
	@Autowired
	private IStyleTemplateService styleTemplateService;
	
	@Autowired
	private IMessageSender messageSender;
	
	@RequestMapping(value = "/sys/interface/testSoap")
    @ResponseBody
    public ResponseData testSoap(HttpServletRequest request) throws Exception {
		Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_21);
		
		//传对象,直接template.process("对象map", writer);
		/*Map<String,Object> element = new HashMap<>();
		element.put("userName", "meixl");
		element.put("phone", "15021625324");*/
		
		//传list
		Map<String,List<User>> map = new HashMap<>();
		List<User> list = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			User user = new User();
			user.setUserName("meixl"+i);
			user.setPhone("15021625324"+i);
			list.add(user);
		}
		map.put("elements", list);
		
		String sayDataTemplate = "Test_Interface_SayData"; //dto
		String sayTemplate = "Test_Interface_Say"; // list
		
		//模板加载
		StyleTemplate styleTemplate = styleTemplateService.getStyleTemplate(sayTemplate);
		if(styleTemplate == null){
			throw new IllegalArgumentException("Invalid TemplateCode");
		}
		String templateString = styleTemplate.getTemplateContent();
		
		//渲染
		StringWriter writer = new StringWriter(1024);
		Template template = new Template(sayTemplate, new StringReader(templateString), freeMarkerConfig);
		// 绑定
		template.process(map, writer);
		String requestXML =writer.toString();
		
		HttpURLConnection conn = null;
		String operation = "say";
		String urlStr = "http://192.168.10.32:8081/system/ws/helloWord/";
        
		byte[] reqload = requestXML.getBytes();
		URL server = new URL(urlStr.trim());
		conn = (HttpURLConnection) server.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("name", "meixl");
		conn.setRequestProperty("pass", "meixl");
		conn.setRequestProperty("WSS-Password Type", "PasswordText");
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		String reqLen = Integer.toString(reqload.length);
		conn.setRequestProperty("Content-Length", reqLen);
		conn.setRequestProperty("SOAPAction", operation);
		OutputStream os = conn.getOutputStream();
		os.write(reqload);
		os.flush();
		System.out.println("status ="+conn.getResponseCode());
		InputStream is = conn.getInputStream();
		BufferedReader reader = null;
		String rs = null;
		reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String strRead = null;
		StringBuffer sb = new StringBuffer();
		while ((strRead = reader.readLine()) != null) {
			sb.append(strRead);
		}
		rs = sb.toString();
		System.out.println("soap 响应报文    start---------");
		System.out.println(rs);
		System.out.println("soap 响应报文    end----------");
		
		InputStream inputStream;
		//构造输入流
		try {
			inputStream = new ByteArrayInputStream(rs.getBytes("UTF-8"));
		} catch (Exception encodeXmlE) {
			throw new Exception(encodeXmlE.getMessage(), encodeXmlE);
		}
		/*InputSource source = new InputSource(new StringReader(rs));
        source.setEncoding("UTF-8");*/
		//解析xml
		Document document;
		try {
			//document = DocumentHelper.parseText(rs);
			document = XmlUtil.mulThreadParse(inputStream);
		} catch (Exception parseXmlE) {
			throw new Exception(parseXmlE.getMessage(), parseXmlE);
		}
		//阅读报文
		//Element root = document.getRootElement();
		Element root = document.getDocumentElement();
		NodeList resNodeList = root.getElementsByTagName("userName");
		for (int i = 0; i < resNodeList.getLength(); i++) {
			String userName = resNodeList.item(i).getTextContent();
			System.out.println("响应报文中解析出 userName = "+ userName);
		}
		conn.disconnect();
		
		messageSender.sendTopic(resNodeList.item(0).getTextContent(), "test.soap.topic");
        
        return new ResponseData(true);
    }
	
}
