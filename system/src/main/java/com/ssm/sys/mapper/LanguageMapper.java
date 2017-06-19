package com.ssm.sys.mapper;

import java.util.List;
import java.util.Map;

import com.ssm.sys.dto.Language;

import com.ssm.mybatis.common.Mapper;

public interface LanguageMapper extends Mapper<Language>{
	
	List<Language> selectFor(Map<String,String> map);

}
