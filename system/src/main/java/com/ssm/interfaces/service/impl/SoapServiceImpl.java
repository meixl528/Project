package com.ssm.interfaces.service.impl;

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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.account.dto.User;
import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.core.request.IRequest;
import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.fnd.service.IStyleTemplateService;
import com.ssm.interfaces.dto.InterfaceLog;
import com.ssm.interfaces.dto.InterfaceResponce;
import com.ssm.interfaces.service.IInetfaceLogService;
import com.ssm.interfaces.service.ISoapService;
import com.ssm.util.XmlUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @name        SoapServiceImpl
 * @description soap接口
 * @author      meixl
 * @date        2017年7月3日下午4:26:20
 * @version
 */
@Service
public class SoapServiceImpl implements ISoapService {
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private IStyleTemplateService styleTemplateService;
	@Autowired
	private IMessageSender messageSender;
	@Autowired
	private IInetfaceLogService inetfaceLogService;
	
	public static final Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_21);
	
	@Override
	public InterfaceResponce<?> doProgress(IRequest iRequest,String interfaceUrl,Map<String,Object> mapDatas,String temp,String loginName,String loginPass) throws Exception{
		
		//模板加载
		StyleTemplate styleTemplate = styleTemplateService.getStyleTemplate(temp);
		if(styleTemplate == null){
			throw new IllegalArgumentException("Invalid TemplateCode");
		}
		String templateString = styleTemplate.getTemplateContent();
		
		//渲染
		StringWriter writer = new StringWriter(1024);
		Template template = new Template(temp, new StringReader(templateString), freeMarkerConfig);
		// 绑定
		template.process(mapDatas, writer);
		String requestXML =writer.toString();
		
		//String urlStr = "http://192.168.10.27:8081/system/ws/helloWord/";
		byte[] reqload = requestXML.getBytes();
		URL server = new URL(interfaceUrl.trim());
		HttpURLConnection conn = (HttpURLConnection) server.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("name", loginName);
		conn.setRequestProperty("pass", loginPass);
		conn.setRequestProperty("WSS-Password Type", "PasswordText");
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		String reqLen = Integer.toString(reqload.length);
		conn.setRequestProperty("Content-Length", reqLen);
		/*conn.setRequestProperty("SOAPAction", "say");*/
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
		
		List<User> list = new ArrayList<>();
		for (int i = 0; i < resNodeList.getLength(); i++) {
			String userName = resNodeList.item(i).getTextContent();
			System.out.println("响应报文中解析出 userName = "+ userName);
			
			User u= new User(userName,null);
			list.add(u);
		}
		conn.disconnect();
		
		try {
			messageSender.sendTopic(resNodeList.item(0).getTextContent(), "test.soap.topic");
		} finally{
			//write log  接口调用日志记录
			String statusCode = root.getElementsByTagName("statusCode").item(0).getTextContent();
			inetfaceLogService.insertSelective(iRequest, new InterfaceLog(interfaceUrl, temp, statusCode, objectMapper.writeValueAsString(list)));
		}
		return new InterfaceResponce<>(list);
		
	}

}
