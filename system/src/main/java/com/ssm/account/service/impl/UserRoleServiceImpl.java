package com.ssm.account.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.ssm.account.dto.UserRole;
import com.ssm.account.mapper.UserRoleMapper;
import com.ssm.account.service.IUserRoleService;
import com.ssm.core.request.IRequest;
import com.ssm.sys.service.impl.BaseServiceImpl;
/**
 * @name        UserRoleServiceImpl
 * @description 
 * @author      meixl
 * @date        2017年5月8日下午4:01:43
 * @version
 */
@Service
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole> implements IUserRoleService {

	@Autowired
	private UserRoleMapper userRoleMapper;
	
	/**
	 * 插叙用户角色
	 */
	@Override
	public List<UserRole> selectUserRoles(IRequest requestContext, UserRole userRole,int page,int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<UserRole> list = userRoleMapper.selectUserRoles(userRole);
		return list;
	}

	

}
