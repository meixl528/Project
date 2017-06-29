package testCXF;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CXFClient {
	
	public static void main(String[] args) throws Exception {
		
		Map<String, String> headArg = new HashMap<String, String>();
		headArg.put("Content-Type", "text/xml;charset=UTF-8");
		headArg.put("SOAPAction", "http://sap.com/xi/WebService/soap1.1");
		headArg.put("User-Agent", "HAP");
		/*headArg.put("Content-Type", "application/json;charset=UTF-8");
		headArg.put("User-Agent", "HAP");*/
		
		String interfaceAddress = "http://192.168.10.32:8081/system/ws/helloWord/say";
		String xml = "<wsdl:definitions xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:tns=\"http://impl.service.webservice.interfaces.ssm.com/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:ns2=\"http://schemas.xmlsoap.org/soap/http\" xmlns:ns1=\"http://service.webservice.interfaces.ssm.com/\" name=\"HelloWord\" targetNamespace=\"http://impl.service.webservice.interfaces.ssm.com/\">"
					+"<wsdl:import location=\"http://192.168.10.32:8081/system/ws/helloWord?wsdl=HelloWordService.wsdl\" namespace=\"http://service.webservice.interfaces.ssm.com/\"></wsdl:import>"
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
					+"<soap:address location=\"http://192.168.10.32:8081/system/ws/helloWord\"/>"
					+"</wsdl:port>"
					+"</wsdl:service>"
					+"</wsdl:definitions>";
		
		
		String xml2 = "<soapenv:ENVELOPE xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sen=\"http://192.168.10.32:8081/system/ws/helloWord/say\">"
					  +"<soapenv:Header/>"
					   +"<soapenv:Body>"
					      +"<sen:MT_OVERDUE_SENDER_REQ>"
					         +"<CREDITCONTROLAREA>1021</CREDITCONTROLAREA>"
					         +"<CUSTOMER_NO>123</CUSTOMER_NO>"
					      +"</sen:MT_OVERDUE_SENDER_REQ>"
					   +"</soapenv:Body>"
					+"</soapenv:ENVELOPE>";
		
		//执行
		/*Map<String, Object> res = new HashMap<>();
		try {
			res = HttpUtil.net(interfaceAddress, xml2, HttpMethod.POST, headArg);
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException externalE) {
			externalE.printStackTrace();
		}
		System.out.println("res = "+res.get("result"));*/
		
		
		SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();;
		SOAPConnection connection = soapConnFactory.createConnection();
		
		//Next, create the actual message
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();//创建soap请求
        
        //Create objects for the message parts            
        SOAPPart soapPart = message.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody body = envelope.getBody();       
        
//        //Populate the body
//        //Create the main element and namespace
        SOAPElement bodyElement = body.addChildElement(envelope.createName("say" , "ns1", "http://webservice.interfaces.ssm.com/"));
        //Add content
        SOAPElement firstElemnt = bodyElement.addChildElement("name");
        Name firstName = envelope.createName("type");
        firstElemnt.addAttribute(firstName, "String");
        firstElemnt.addTextNode("1");

        /*SOAPElement secondElemnt = bodyElement.addChildElement("in1");
        Name secondName = envelope.createName("type");
        secondElemnt.addAttribute(secondName, "int");
        secondElemnt.addTextNode("0");*/
              
        //Save the message
        message.saveChanges();
        //Check the input
        message.writeTo(System.out);
        System.out.println();
       //Send the message and get a reply   
           
       //Set the destination
       String destination = 
             "http://192.168.10.32:8081/system/ws/helloWord/";
       //Send the message
       SOAPMessage reply = connection.call(message, destination);
       System.out.println("------------------");
       System.out.println(reply);
       System.out.println("------------------");
       if(reply!=null)
       {
            SOAPPart replySP = reply.getSOAPPart();
            SOAPEnvelope replySE = replySP.getEnvelope();
            SOAPBody replySB = replySE.getBody();

            Source source = reply.getSOAPPart().getContent();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            ByteArrayOutputStream myOutStr = new ByteArrayOutputStream();
            StreamResult res = new StreamResult();
            res.setOutputStream(myOutStr);
            transformer.transform(source,res);
            String temp = myOutStr.toString("UTF-8");
           
            System.out.println(temp);
            byte[] bytes = temp.getBytes("UTF-8");
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            
            DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            Document doc = null;
            db = dbf.newDocumentBuilder();
            doc = db.parse(in);
            Element docEle = doc.getDocumentElement();
            NodeList nl = docEle.getElementsByTagName("ns2:http://schemas.xmlsoap.org/soap/http");
           if(nl != null && nl.getLength() > 0) 
           {
               for(int i = 0 ; i < nl.getLength();i++) 
               {
                   //get the employee element
                   Element el = (Element)nl.item(i);
                   String name = getTextValue(el,"name");
                   int id = getIntValue(el,"userId");
                   System.out.println("name: "+name+" id: "+id);
               }
           }    
       }
        //Close the connection            
        connection.close();
	}
	
	private static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }
    /**
     * Calls getTextValue and returns a int value
     * @param ele
     * @param tagName
     * @return
     */
    private static int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele,tagName));
    }
    
    private static void parseXmlFile(String fileName)
    {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            //parse using builder to get DOM representation of the XML file
            Document dom = db.parse(fileName);
        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch(SAXException se) {
            se.printStackTrace();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
