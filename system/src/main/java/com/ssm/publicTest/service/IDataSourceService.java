package com.ssm.publicTest.service;

import java.util.List;

import com.ssm.account.dto.User;
import com.ssm.core.proxy.ProxySelf;

public interface IDataSourceService extends ProxySelf<IDataSourceService>{
	
	public List<User> testWriteDS();
	
	public List<User> testReadDS();
}
