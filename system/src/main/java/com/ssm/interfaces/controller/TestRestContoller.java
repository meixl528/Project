package com.ssm.interfaces.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class TestRestContoller extends BaseRestController{
	
	@RequestMapping(value = "/restapi/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String query(@RequestParam String name) throws JsonProcessingException {
		System.out.println("name = "+name);
		return "oooo";
	}
	
}