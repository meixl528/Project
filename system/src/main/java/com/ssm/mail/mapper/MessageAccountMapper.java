package com.ssm.mail.mapper;

import java.util.List;

import com.ssm.mail.dto.MessageAccount;
import com.ssm.mybatis.common.Mapper;

public interface MessageAccountMapper extends Mapper<MessageAccount> {

    MessageAccount selectByUniqueCode(String accountCode);

    List<MessageAccount> selectMessageAccountPassword(MessageAccount example);
}