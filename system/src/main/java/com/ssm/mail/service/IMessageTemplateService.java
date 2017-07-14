package com.ssm.mail.service;

import java.util.List;

import com.ssm.core.annotation.StdWho;
import com.ssm.core.exception.EmailException;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageTemplate;

/**
 * 消息模板服务接口.
 */
public interface IMessageTemplateService extends ProxySelf<IMessageTemplateService> {

    MessageTemplate createMessageTemplate(IRequest request, @StdWho MessageTemplate obj) throws EmailException;

    MessageTemplate updateMessageTemplate(IRequest request, @StdWho MessageTemplate obj) throws EmailException;

    MessageTemplate selectMessageTemplateById(IRequest request, Long objId);

    List<MessageTemplate> selectMessageTemplates(IRequest request, MessageTemplate obj, int page, int pageSize);

    int deleteMessageTemplate(IRequest request, MessageTemplate obj);

    int batchDelete(IRequest request, List<MessageTemplate> objs);

}
