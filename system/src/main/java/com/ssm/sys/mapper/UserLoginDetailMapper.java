package com.ssm.sys.mapper;

import com.ssm.sys.dto.UserLoginDetail;

import com.ssm.mybatis.common.Mapper;

public interface UserLoginDetailMapper extends Mapper<UserLoginDetail>{
	
	void saveDetail(UserLoginDetail userLoginDetail);

}
