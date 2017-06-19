package com.ssm.account.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssm.account.dto.User;
import com.ssm.mybatis.common.Mapper;

public interface UserMapper extends Mapper<User>{
	
	User selectByUserName(String userName);

    List<User> selectByIdList(List<Long> userIds);

    int updatePassword(@Param("userId") Long userId,@Param("password") String passwordNew);
    
    int updateFirstLogin(@Param("userId") Long userId,@Param("status") String status);

    List<User> selectUsers(User user);
}
