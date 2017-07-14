package com.ssm.mail.mapper;

import com.ssm.mail.dto.MessageTransaction;
import com.ssm.mybatis.common.Mapper;

/**
 */
public interface MessageTransactionMapper extends Mapper<MessageTransaction> {

    int deleteByMessageId(Long messageId);

    long selectSuccessCountByMessageId(Long messageId);
}