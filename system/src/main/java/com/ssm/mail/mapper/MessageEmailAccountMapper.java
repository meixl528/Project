package com.ssm.mail.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssm.mail.dto.MessageEmailAccount;
import com.ssm.mail.dto.MessageEmailAccountVo;
import com.ssm.mybatis.common.Mapper;


/**
 */
public interface MessageEmailAccountMapper extends Mapper<MessageEmailAccount> {
    
    int deleteByConfigId(Long configId);
    
    List<MessageEmailAccountVo> selectMessageEmailAccounts(MessageEmailAccount record);

    List<MessageEmailAccount> selectMessageEmailAccountPassword(MessageEmailAccount record);
    
    MessageEmailAccount selectByAccountCode(String accountCode);
    
    MessageEmailAccount getMsgEmailAccountByCode(@Param("accountId") Long accountId,
            @Param("accountCode") String accountCode);
}