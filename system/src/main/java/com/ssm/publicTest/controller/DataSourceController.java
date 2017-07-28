package com.ssm.publicTest.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.account.dto.User;
import com.ssm.account.service.IUserService;
import com.ssm.core.request.IRequest;
import com.ssm.publicTest.service.IDataSourceService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 读写 数据源配置(jndi)
 * @name        DataSourceTest
 * @description 读写数据源readWriteDataSource & readOnlyDataSource
 * @see         com.ssm.core.jndi.db.DynamicDataSource
 * @author      meixl
 * @date        2017年7月17日上午9:48:43
 */
@Controller
public class DataSourceController extends BaseController{

	@Autowired
	private IDataSourceService dataSourceService;
	
	@Autowired
	private IUserService userService;
	
	@RequestMapping(value = "/db/test/write")
    @ResponseBody
	public ResponseData testWriteDS(HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		userService.insertSelective(requestContext, new User(new DateTime().toString(),"test"));
		
		List<User> list= dataSourceService.testWriteDS();
		return new ResponseData(list);
	}
	
	@RequestMapping(value = "/db/test/read")
    @ResponseBody
	public ResponseData testReadDS(HttpServletRequest request){
		List<User> list = dataSourceService.testReadDS();
		return new ResponseData(list);
	}
	
}
