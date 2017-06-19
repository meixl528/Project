package com.ssm.function.mapper;

import com.ssm.function.dto.RoleFunction;

import com.ssm.mybatis.common.Mapper;

public interface RoleFunctionMapper extends Mapper<RoleFunction>{
	
	void deleteByRoleId(Long roleId);

}
