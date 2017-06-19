package com.ssm.function.mapper;

import com.ssm.function.dto.Resource;

import com.ssm.mybatis.common.Mapper;

public interface ResourceMapper extends Mapper<Resource>{
	
	Resource selectResourceByUrl(String url);
}
