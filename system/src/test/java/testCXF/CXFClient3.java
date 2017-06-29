package testCXF;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/*import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;*/
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.fnd.service.ISequenceService;
import com.ssm.fnd.service.IStyleTemplateService;
import com.ssm.util.XmlUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @name        CXFClient3
 * @description soap发送请求报文,解析返回报文
 * @author      meixl
 * @date        2017年6月27日下午1:36:05
 * @version
 */
public class CXFClient3 {

	public static void main(String[] args) throws Exception {
		
		HttpURLConnection conn = null;
		String operation = "say";
		String urlStr = "http://192.168.10.32:8081/system/ws/helloWord/";
		String requestXML = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				            +"<SOAP-ENV:Header/>"
							+"<SOAP-ENV:Body>"
							+"<ns1:say xmlns:ns1=\"http://webservice.interfaces.ssm.com/\">"
							+"<name type=\"string\">1</name>"
							+"</ns1:say>"
							+"</SOAP-ENV:Body>"
							+"</SOAP-ENV:Envelope>";

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
		System.out.println("--------"+conn.getResponseCode() +"-----------");
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
		System.out.println("=========="+rs+"==========");
		
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
		NodeList resNodeList = root.getElementsByTagName("aaa");
		String aaaa = resNodeList.item(0).getTextContent();
		
		System.out.println(aaaa);
		conn.disconnect();
	}

	
}
