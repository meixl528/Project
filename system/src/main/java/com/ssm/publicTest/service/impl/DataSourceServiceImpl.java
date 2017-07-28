package com.ssm.publicTest.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssm.account.dto.User;
import com.ssm.account.service.IUserService;
import com.ssm.publicTest.service.IDataSourceService;
/**
 * 测试读写库 分离
 * @name        DataSourceImpl
 * @description service不能加 @Transactional
 * @author      meixl
 * @date        2017年7月17日上午10:07:58
 */
@Service
public class DataSourceServiceImpl implements IDataSourceService{
	
	@Autowired
	private IUserService userService;

	@Override
	public List<User> testWriteDS() {
		return userService.selectAll();
	}

	@Override
	@com.ssm.core.jndi.db.ReadDataSource
	public List<User> testReadDS() {
		return userService.selectAll();
	}

}
