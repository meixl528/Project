package com.ssm.sys.service;

import com.ssm.core.proxy.ProxySelf;
import com.ssm.sys.dto.SysConfig;

public interface ISysConfigService extends IBaseService<SysConfig>, ProxySelf<ISysConfigService> {
    /**
     * 根据ConfigCode获取配置值.
     * 
     * @param configCode
     *            配置代码
     * @return 配置值
     */
            
    String getConfigValue(String configCode);
    
}