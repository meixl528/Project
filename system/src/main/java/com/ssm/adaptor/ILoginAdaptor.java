/*
 * #{copyright}#
 */
package com.ssm.adaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.ssm.account.dto.User;
import com.ssm.account.exception.RoleException;
import com.ssm.account.exception.UserException;
import com.ssm.core.exception.BaseException;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 登陆代理接口类.
 * 
 * @author meixl
 */
public interface ILoginAdaptor {

    /**
     * 超时登陆逻辑.
     * 
     * @param account
     *            登陆账号对象
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return ResponseData
     * @throws RoleException 
     * @throws BaseException
     */
    ResponseData sessionExpiredLogin(User account, HttpServletRequest request, HttpServletResponse response) throws RoleException;

    /**
     * 
     * 角色选择逻辑.
     * 
     * @param role
     *            角色对象
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return view
     * @throws RoleException
     */
    ModelAndView doSelectRole(Long roleId, HttpServletRequest request, HttpServletResponse response) throws RoleException;

    /**
     * 显示主界面.
     *
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return view
     */
    ModelAndView indexView(HttpServletRequest request, HttpServletResponse response);

    /**
     * 登陆界面.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return view
     * @throws UserException 
     */
    ModelAndView loginView(User user,HttpServletRequest request, HttpServletResponse response) throws UserException;

    /**
     * 显示角色选择界面.
     * 
     * @param request
     *            HttpServletRequest
     * @param response
     *            HttpServletResponse
     * @return view viewModel
     * 
     * @throws BaseException
     *             BaseException
     */
    ModelAndView roleView(HttpServletRequest request, HttpServletResponse response);

    /**
     * 退出登录
     */
	ModelAndView logout(HttpServletRequest request, HttpServletResponse response);

}
