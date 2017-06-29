package com.ssm.interfaces.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.Produces;

import com.ssm.interfaces.dto.Data;
import com.ssm.interfaces.dto.InterfaceRequest;
import com.ssm.interfaces.dto.InterfaceResponce;
/**
 * @name        HelloWordService
 * @description soap xml格式
 * @author      meixl
 * @date        2017年6月29日上午10:43:44
 * @version
 */
@WebService
@Produces("charset=UTF-8")
public interface HelloWordService {

	/**  soap  */
	@WebMethod (action = "say", operationName = "say")
	@WebResult (name = "result")
	InterfaceResponce<Data> say(@WebParam(name = "arg") InterfaceRequest request);

	@WebMethod (action = "sayData", operationName = "sayData")
	@WebResult (name = "result")
	InterfaceResponce<Data> sayData(Data da);
}
