package com.ssm.mail.service;

import java.util.List;

import com.ssm.core.annotation.StdWho;
import com.ssm.core.exception.BaseException;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageEmailAccount;
import com.ssm.mail.dto.MessageEmailAccountVo;

/**
 * 邮件账号服务接口.
 */
public interface IMessageEmailAccountService extends ProxySelf<IMessageEmailAccountService> {

    MessageEmailAccount createMessageEmailAccount(IRequest request, @StdWho MessageEmailAccount obj) throws BaseException;

    MessageEmailAccount updateMessageEmailAccount(IRequest request, @StdWho MessageEmailAccount obj);

    MessageEmailAccount updateMessageEmailAccountPasswordOnly(IRequest request, MessageEmailAccount obj);
    
    MessageEmailAccount selectMessageEmailAccountById(IRequest request, Long objId);

    List<MessageEmailAccountVo> selectMessageEmailAccounts(IRequest request, MessageEmailAccount obj, int page, int pageSize);

    int deleteMessageEmailAccount(IRequest request, MessageEmailAccount obj);

    int batchDelete(IRequest request, List<MessageEmailAccount> objs) throws BaseException;
    
    List<MessageEmailAccount> selectMessageEmailAccountWithPassword(IRequest request, MessageEmailAccount obj, int page, int pageSize);
    
}
