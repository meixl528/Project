package testCXF;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.ssm.fnd.utils.HttpUtil;
import com.ssm.fnd.utils.HttpUtil.HttpMethod;

public class CXFClient {
	
	public static void main(String[] args) {
		
		Map<String, String> headArg = new HashMap<String, String>();
		/*headArg.put("Content-Type", "text/xml;charset=UTF-8");
		headArg.put("SOAPAction", "http://sap.com/xi/WebService/soap1.1");
		headArg.put("User-Agent", "HAP");*/
		headArg.put("Content-Type", "application/json;charset=UTF-8");
		headArg.put("User-Agent", "HAP");
		
		String interfaceAddress = "http://localhost:8081/system/ws/helloWord/say";
		String xml = "<wsdl:definitions xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:tns=\"http://impl.service.webservice.interfaces.ssm.com/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:ns2=\"http://schemas.xmlsoap.org/soap/http\" xmlns:ns1=\"http://service.webservice.interfaces.ssm.com/\" name=\"HelloWord\" targetNamespace=\"http://impl.service.webservice.interfaces.ssm.com/\">"
					+"<wsdl:import location=\"http://localhost:8081/system/ws/helloWord?wsdl=HelloWordService.wsdl\" namespace=\"http://service.webservice.interfaces.ssm.com/\"></wsdl:import>"
					+"<wsdl:binding name=\"HelloWordSoapBinding\" type=\"ns1:HelloWordService\">"
					+"<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>"
					+"<wsdl:operation name=\"say\">"
					+"<soap:operation soapAction=\"say\" style=\"document\"/>"
					+"<wsdl:input name=\"say\">"
					+"<soap:body use=\"literal\"/>"
					+"</wsdl:input>"
					+"<wsdl:output name=\"sayResponse\">"
					+"<soap:body use=\"literal\"/>"
					+"</wsdl:output>"
					+"</wsdl:operation>"
					+"</wsdl:binding>"
					+"<wsdl:service name=\"HelloWord\">"
					+"<wsdl:port binding=\"tns:HelloWordSoapBinding\" name=\"HelloWordServiceImplPort\">"
					+"<soap:address location=\"http://localhost:8081/system/ws/helloWord\"/>"
					+"</wsdl:port>"
					+"</wsdl:service>"
					+"</wsdl:definitions>";
		
		//执行
		Map<String, Object> res = new HashMap<>();
		try {
			res = HttpUtil.net(interfaceAddress, null, HttpMethod.POST, headArg);
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException externalE) {
			externalE.printStackTrace();
		}
		System.out.println("res = "+res.get("result"));
	}
	
	

}
