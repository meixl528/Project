package com.ssm.qq.controller;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qq.connect.QQConnectException;
import com.qq.connect.oauth.Oauth;
import com.qq.connect.utils.QQConnectConfig;

@Controller
public class QqLoginController {
	
	static {
    	Properties pro = new Properties();
		try {
			pro.load(QqLoginController.class.getClassLoader().getResourceAsStream("/qqconnectconfig.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		pro.forEach((k,v) -> {
			if(StringUtils.isNotBlank((String)k) && StringUtils.isNotBlank((String)v)){
    			QQConnectConfig.updateProperties((String)k, (String)v);
    		}
		});
    }
	
	@RequestMapping(value = "/qqLogin@Login")
	public void login(HttpServletRequest request,HttpServletResponse response) throws IOException {
		request.getSession().setAttribute("qq_connect_state", new Random());
        response.setContentType("text/html;charset=utf-8");
        try {
        	response.sendRedirect(new Oauth().getAuthorizeURL(request));
        } catch (QQConnectException e) {
        	//return false;
        }
        //return new ModelAndView(new StringBuilder("/").toString());
        //return true;
	}

}
