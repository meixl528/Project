package com.ssm.function.mapper;

import com.ssm.function.dto.FunctionResource;
import com.ssm.function.dto.Resource;

import com.ssm.mybatis.common.Mapper;

public interface FunctionResourceMapper extends Mapper<FunctionResource>{
	
	void deleteByResource(Resource resource);

}
