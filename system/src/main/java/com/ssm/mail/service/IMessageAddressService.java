package com.ssm.mail.service;

import java.util.List;

import com.ssm.mail.MessageTypeEnum;
import com.ssm.mail.dto.MessageAddress;

/**
 */
public interface IMessageAddressService {
    /**
     * 
     * 返回消息地址列表.
     * 
     * @param msgType
     *            消息类型 MessageTypeEnum
     * @return 地址列表
     */
    List<String> queryMessageAddress(MessageTypeEnum msgType, MessageAddress address);
}
