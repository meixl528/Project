package com.ssm.publicTest.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ssm.sys.responceFactory.ResponseData;
import com.google.common.collect.Lists;

/**
 * @name        ThreadLocalController
 * @description 
 * @author      meixl
 * @date        2017年8月30日下午5:02:34
 * @version
 */
@Controller
//@Scope("prototype")  多例属性, controller默认是单例的,提高性能
public class ThreadLocalController {
	
	ThreadLocal<String> tl = new ThreadLocal<>();

	@RequestMapping(value="/threadLocal/test")
	//@ResponseBody
    public ResponseData test(HttpServletRequest request,HttpServletResponse response) throws Exception{
        ResponseData res = new ResponseData();
        
        System.out.println("get ----- "+ tl.get());
        
        tl.set(Thread.currentThread().getName());
        System.out.println("set ----- "+ Thread.currentThread().getName());
        
        return res;
    }
	
	@RequestMapping(value="/threadLocal/test2")
	//@ResponseBody
    public ResponseData test2(HttpServletRequest request,HttpServletResponse response) throws Exception{
        ResponseData res = new ResponseData();
        
        System.out.println("get ----- "+ tl.get());
        
        tl.set(Thread.currentThread().getName());
        System.out.println("set ----- "+ Thread.currentThread().getName());
        
        return res;
    }
	
}
