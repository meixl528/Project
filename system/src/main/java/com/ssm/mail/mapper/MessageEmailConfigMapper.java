package com.ssm.mail.mapper;

import java.util.List;

import com.ssm.mail.dto.MessageEmailConfig;
import com.ssm.mybatis.common.Mapper;


/**
 */
public interface MessageEmailConfigMapper extends Mapper<MessageEmailConfig> {

    List<MessageEmailConfig> selectMessageEmailConfigs(MessageEmailConfig record);

    Integer queryMsgConfigQuanties();
}