package com.ssm.mail.mapper;

import java.util.List;

import com.ssm.mail.dto.MessageEmailWhiteList;
import com.ssm.mybatis.common.Mapper;

/**
 */
public interface MessageEmailWhiteListMapper extends Mapper<MessageEmailWhiteList> {

    int deleteByConfigId(Long configId);
    
    List<MessageEmailWhiteList> selectByConfigId(Long configId);

}