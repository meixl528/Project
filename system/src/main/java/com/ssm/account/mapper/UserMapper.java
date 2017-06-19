package com.ssm.account.mapper;

import java.util.List;

import com.ssm.account.dto.User;

import com.ssm.mybatis.common.Mapper;

public interface UserMapper extends Mapper<User>{
	
	List<User> getUserList(User user);
	
	User getUser(User user);
	
	User selectByUserName(String name);

	void updateUser(User user);

}
