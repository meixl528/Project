package com.ssm.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.cache.annotation.CacheSet;
import com.ssm.core.request.IRequest;
import com.ssm.sys.dto.GlobalProfile;
import com.ssm.sys.dto.SysConfig;
import com.ssm.sys.mapper.SysConfigMapper;
import com.ssm.sys.service.ISysConfigService;

/**
 * @name        ISysConfigService
 * @description 系统配置
 * @author      meixl
 * @date        2017年8月8日上午10:54:48
 * @version
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfig> implements ISysConfigService { 

    @Autowired
    private SysConfigMapper configMapper;
    
    @Autowired
    private IMessageSender messageSender;
    
	@Override
    @CacheSet(cache = "config")
    public SysConfig insertSelective(IRequest request, SysConfig config) {
        super.insertSelective(request, config);
        //配置更改时 通知监听者
        messageSender.sendTopic(new GlobalProfile(config.getConfigCode(), config.getConfigValue()),"config");
        return config;
    }

    /*@Override
    @CacheDelete(cache = "config")
    public int deleteByPrimaryKey(Config config) {
        return super.deleteByPrimaryKey(config);
    }*/

    @Override
    @CacheSet(cache = "config")
    public SysConfig updateByPrimaryKeySelective(IRequest request, SysConfig config) {
        super.updateByPrimaryKeySelective(request, config);
        //配置更改时 通知监听者
        messageSender.sendTopic(new GlobalProfile(config.getConfigCode(), config.getConfigValue()),"config");
        return config;
    }

    @Override
    public String getConfigValue(String configCode) { 
        SysConfig config = configMapper.selectByCode(configCode);
        if(config !=null){
            return config.getConfigValue();
        }else return null;
    }
}