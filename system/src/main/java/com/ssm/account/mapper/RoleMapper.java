package com.ssm.account.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssm.account.dto.Role;
import com.ssm.account.dto.RoleExt;
import com.ssm.account.dto.User;

import com.ssm.mybatis.common.Mapper;


public interface RoleMapper extends Mapper<Role>{
	
	List<RoleExt> selectByUser(User user);
	
	//校验用户角色是否存在
	List<Role> selectUserRoleCount(@Param(value = "locale")String locale,@Param(value = "userId") Long userId,@Param(value = "roleId") Long roleId);

	/**
	 * 查询角色
	 * @param role
	 * @return
	 */
	List<Role> selectRole(Role role);

	/**
	 * 查询用户没有的角色
	 * @param roleExt
	 * @return
	 */
	List<Role> selectRoleNotUserRoles(Role roleExt);
	
}
