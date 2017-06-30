package com.ssm.fnd.mapper;

import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.mybatis.common.Mapper;

/**
 * @name        StyleTemplateMapper
 * @description 样式模版管理数据操作Mapper类
 * @author      meixl
 */
public interface StyleTemplateMapper extends Mapper<StyleTemplate>{

	StyleTemplate selectStyleTemplateFromDB(String templateCode);
	
}