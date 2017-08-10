package com.ssm.account.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ssm.account.dto.User;
import com.ssm.account.exception.UserException;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.sys.service.IBaseService;

public interface IUserService extends IBaseService<User>, ProxySelf<IUserService>{

	List<User> submitUser(HttpServletRequest request, IRequest iRequest, List<User> userList);
	/**
	 * 验证用户名是否存在
	 * @param list
	 * @return
	 */
	String validateUser(List<User> list);
	
	int removeUser(List<User> list);
	
	
	User login(User user) throws UserException;
}
