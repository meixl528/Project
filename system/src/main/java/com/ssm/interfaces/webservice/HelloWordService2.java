package com.ssm.interfaces.webservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ssm.interfaces.dto.Data;
import com.ssm.interfaces.dto.InterfaceResponce;
/**
 * @name        HelloWordService2
 * @description restful  json格式
 * @author      meixl
 * @date        2017年6月29日上午10:44:05
 * @version
 */
@Path(value = "rest")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public interface HelloWordService2 {
	
	/**  rest  */
	@POST
	@Path(value = "json")
	//@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }) 
	@Produces(MediaType.APPLICATION_JSON) 
	InterfaceResponce<Data> rest(Data da);
	
}
