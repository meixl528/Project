package com.ssm.interfaces.webservice.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.Produces;

@WebService
@Produces("charset=UTF-8")
public interface HelloWordService {

	@WebMethod (action = "say", operationName = "say")
	@WebResult (name = "result")
	String say();
	
}
