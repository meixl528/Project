package com.ssm.mail.service;

import java.util.List;

import com.ssm.core.annotation.StdWho;
import com.ssm.core.exception.BaseException;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageEmailWhiteList;

/**
 * 邮箱白名单服务接口.
 */
public interface IMessageEmailWhiteListService extends ProxySelf<IMessageEmailWhiteListService> {

    MessageEmailWhiteList createMessageEmailWhiteList(IRequest request, @StdWho MessageEmailWhiteList obj) throws BaseException;

    MessageEmailWhiteList updateMessageEmailWhiteList(IRequest request, @StdWho MessageEmailWhiteList obj);

    MessageEmailWhiteList selectMessageEmailWhiteListById(IRequest request, Long objId);

    List<MessageEmailWhiteList> selectMessageEmailWhiteLists(IRequest request, MessageEmailWhiteList obj, int page, int pageSize);

    int deleteMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj);

    int batchDelete(IRequest request, List<MessageEmailWhiteList> objs) throws BaseException;
    
}
