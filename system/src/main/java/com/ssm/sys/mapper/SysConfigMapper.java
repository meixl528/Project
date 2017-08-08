package com.ssm.sys.mapper;

import com.ssm.mybatis.common.Mapper;
import com.ssm.sys.dto.SysConfig;

public interface SysConfigMapper extends Mapper<SysConfig> {

    SysConfig selectByCode(String configCode);
}