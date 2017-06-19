package com.ssm.function.mapper;

import java.util.List;
import java.util.Map;

import com.ssm.function.dto.Function;
import com.ssm.function.dto.FunctionDisplay;
import com.ssm.function.dto.Resource;

import com.ssm.mybatis.common.Mapper;

public interface FunctionMapper extends Mapper<Function>{
	
	List<Function> selectForCache();

	List<Resource> selectExistsResourcesByFunction(Map<String, Object> params);
	
	List<FunctionDisplay> selectFunctions(Function example);

}
