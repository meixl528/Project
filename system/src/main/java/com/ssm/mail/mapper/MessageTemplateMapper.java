package com.ssm.mail.mapper;

import org.apache.ibatis.annotations.Param;

import com.ssm.mail.dto.MessageTemplate;
import com.ssm.mybatis.common.Mapper;

public interface MessageTemplateMapper extends Mapper<MessageTemplate> {
    MessageTemplate selectByCode(String templateCode);

    MessageTemplate getMsgTemByCode(@Param("templateId") Long templateId, @Param("templateCode") String templateCode);
}