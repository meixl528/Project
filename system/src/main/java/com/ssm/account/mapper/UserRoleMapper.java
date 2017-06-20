package com.ssm.account.mapper;

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

	int deleteByUserId(Long userId);

    int deleteByRecord(UserRole record);

    int deleteByRoleId(Long roleId);
	
	
}
