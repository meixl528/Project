package com.ssm.mail.service;

import java.util.List;

import com.ssm.core.annotation.StdWho;
import com.ssm.core.exception.BaseException;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageAccount;

/**
 * 消息模板服务接口.
 */
public interface IMessageAccountService extends ProxySelf<IMessageAccountService> {

    MessageAccount createMessageAccount(IRequest request, @StdWho MessageAccount obj) throws BaseException;

    MessageAccount updateMessageAccount(IRequest request, @StdWho MessageAccount obj);

    MessageAccount updateMessageAccountPasswordOnly(IRequest request, MessageAccount obj);
    
    MessageAccount selectMessageAccountById(IRequest request, Long objId);

    List<MessageAccount> selectMessageAccounts(IRequest request, MessageAccount obj, int page, int pageSize);

    int deleteMessageAccount(IRequest request, MessageAccount obj);

    int batchDelete(IRequest request, List<MessageAccount> objs) throws BaseException;
    
    List<MessageAccount> selectMessageAccountPassword(IRequest request, MessageAccount obj, int page, int pageSize);
    
}
