package com.ssm.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.ssm.account.dto.User;
import com.ssm.account.exception.RoleException;
import com.ssm.adaptor.ILoginAdaptor;
import com.ssm.sys.responceFactory.ResponseData;

@Controller
public class LoginController extends BaseController{

	@Autowired
    private ILoginAdaptor loginAdaptor;

	/**
     * 显示登陆界面.
     */
    @RequestMapping(value = { "/login.html","/login"})
	public ModelAndView login(final User user,final HttpServletRequest request,final HttpServletResponse response) throws Exception{
		return loginAdaptor.loginView(user,request, response);
	}

    /**
     * 显示角色选择界面.
     */
    @RequestMapping(value = { "/role.html","/role"})
    public ModelAndView roleView(final HttpServletRequest request, final HttpServletResponse response){
        return loginAdaptor.roleView(request, response);
    }
    
    /**
     * 显示主界面.
     */
    @RequestMapping(value = { "/welcome.html","/welcome"})
    public ModelAndView indexView(final HttpServletRequest request, final HttpServletResponse response) {    
        return loginAdaptor.indexView(request, response);
    }
    
    /**
     * 角色选择逻辑.
     * @param role 角色对象
     * @throws RoleException 
     */
    @RequestMapping(value = {"/selectedRole.html","/selectedRole"})
    public ModelAndView selectRole(final Long roleId, final HttpServletRequest request,final HttpServletResponse response) throws RoleException{
        return loginAdaptor.doSelectRole(roleId, request, response);
    }
    
    /**
     * 超时重新登陆.
     * @throws RoleException 
     */
    @RequestMapping(value = "/sessionExpiredLogin", method = RequestMethod.POST)
    public ResponseData sessionExpiredLogin(final User account, final HttpServletRequest request,final HttpServletResponse response) throws RoleException{
        return loginAdaptor.sessionExpiredLogin(account, request, response);
    }
    
    /**
     * 退出登录
     */
	@RequestMapping(value = {"/logout.html","/logout"})
	public ModelAndView logout(final HttpServletRequest request,final HttpServletResponse response){
		return loginAdaptor.logout(request,response);
	}
	
}
