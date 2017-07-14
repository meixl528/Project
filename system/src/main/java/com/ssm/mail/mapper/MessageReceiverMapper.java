package com.ssm.mail.mapper;

import java.util.List;

import com.ssm.mail.dto.MessageReceiver;
import com.ssm.mybatis.common.Mapper;

/**
 */
public interface MessageReceiverMapper extends Mapper<MessageReceiver> {

    int deleteByMessageId(Long messageId);

    List<MessageReceiver> selectByMessageId(Long messageId);

    /*
     * 根据MessageId查询消息地址
     */
    List<MessageReceiver> selectMessageAddressesByMessageId(MessageReceiver messageReceiver);
}