package testCXF;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class CXFClient2 {
	public static void main(String[] args) throws Exception {
		
		JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
		Client client = factory.createClient("http://192.168.10.32:8081/system/ws/helloWord?wsdl");
		Object[] objs = client.invoke("say", "阿福");
		System.out.println(objs[0].toString());
		
	}
}