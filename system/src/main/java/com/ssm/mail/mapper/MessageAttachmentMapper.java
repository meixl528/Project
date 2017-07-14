package com.ssm.mail.mapper;

import java.util.List;

import com.ssm.mail.dto.MessageAttachment;
import com.ssm.mybatis.common.Mapper;

/**
 */
public interface MessageAttachmentMapper extends Mapper<MessageAttachment> {

    int deleteByMessageId(Long messageId);

    List<MessageAttachment> selectByMessageId(Long messageId);
}