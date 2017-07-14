package com.ssm.mail.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.core.exception.EmailException;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageTemplate;
import com.ssm.mail.mapper.MessageTemplateMapper;
import com.ssm.mail.service.IMessageTemplateService;

/**
 * 消息模板impl
 */
@Transactional
@Service
public class MessageTemplateServiceImpl implements IMessageTemplateService {

    @Autowired
    private MessageTemplateMapper templateMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageTemplate createMessageTemplate(IRequest request, MessageTemplate obj) throws EmailException {
        if (obj == null) {
            return null;
        }
        /* Mclin修改，验证编号和市场是否唯一 */
        if (validMsgTem(request, obj) == false) {
            throw new EmailException(EmailException.MSG_ERROR_SAME_CODE_AND_MARKET_ID_IS_EXISTS);
        }
        templateMapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageTemplate updateMessageTemplate(IRequest request, MessageTemplate obj) throws EmailException {
        if (obj == null) {
            return null;
        }
        /* Mclin修改，验证编号和市场是否唯一 */
        if (validMsgTem(request, obj) == false) {
            throw new EmailException(EmailException.MSG_ERROR_SAME_CODE_AND_MARKET_ID_IS_EXISTS);
        }
        templateMapper.updateByPrimaryKeySelective(obj);
        return obj;
    }

    @Override
    public MessageTemplate selectMessageTemplateById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return templateMapper.selectByPrimaryKey(objId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteMessageTemplate(IRequest request, MessageTemplate obj) {
        if (obj.getTemplateId() == null) {
            return 0;
        }
        return templateMapper.deleteByPrimaryKey(obj);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int batchDelete(IRequest request, List<MessageTemplate> objs) {
        int result = 0;
        if (objs == null || objs.isEmpty()) {
            return result;
        }

        for (MessageTemplate obj : objs) {
            self().deleteMessageTemplate(request, obj);
            result++;
        }
        return result;
    }

    @Override
    public List<MessageTemplate> selectMessageTemplates(IRequest request, MessageTemplate example, int page,
            int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<MessageTemplate> list = templateMapper.select(example);
        return list;
    }
    
    private boolean validMsgTem(IRequest request, MessageTemplate obj){
        MessageTemplate mt = templateMapper.getMsgTemByCode(obj.getTemplateId(), obj.getTemplateCode());
        if (mt != null) {
            return false;
        }
        return true;
    }

}
