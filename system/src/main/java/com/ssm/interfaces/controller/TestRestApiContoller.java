package com.ssm.interfaces.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
/**
 * @name        TestRestApiContoller
 * @description 跨域ajax调用测试
 * @author      meixl
 * @date        2017年6月30日上午10:37:02
 * @version
 */
@RestController
public class TestRestApiContoller extends BaseRestApiController{
	
	@RequestMapping(value = "/restapi/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String query(@RequestParam String name) throws JsonProcessingException {
		System.out.println("name = "+name);
		return "ajax调用成功!";
	}
	
}