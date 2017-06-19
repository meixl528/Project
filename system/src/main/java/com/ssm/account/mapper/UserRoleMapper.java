package com.ssm.account.mapper;

import java.util.List;

import com.ssm.account.dto.UserRole;

import com.ssm.mybatis.common.Mapper;
/**
 * @name        UserRoleMapper
 * @description 
 * @author      meixl
 * @date        2017年5月8日下午4:05:45
 * @version
 */
public interface UserRoleMapper extends Mapper<UserRole>{

	/**
	 * 查询用户角色
	 * @param userRole
	 * @return
	 */
	List<UserRole> selectUserRoles(UserRole userRole);
	
	
}
