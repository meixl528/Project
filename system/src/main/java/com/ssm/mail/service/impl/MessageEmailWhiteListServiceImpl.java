package com.ssm.mail.service.impl;

import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageEmailWhiteList;
import com.ssm.mail.mapper.MessageEmailWhiteListMapper;
import com.ssm.mail.service.IMessageEmailWhiteListService;

/**
 * 邮箱白名单impl.
 */
@Transactional
@Service
public class MessageEmailWhiteListServiceImpl implements IMessageEmailWhiteListService, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Autowired
    private MessageEmailWhiteListMapper mapper;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageEmailWhiteList createMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        mapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageEmailWhiteList updateMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        mapper.updateByPrimaryKeySelective(obj);
        return obj;
    }

    @Override
    public MessageEmailWhiteList selectMessageEmailWhiteListById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return mapper.selectByPrimaryKey(objId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteMessageEmailWhiteList(IRequest request, MessageEmailWhiteList obj) {
        if (obj.getId() == null) {
            return 0;
        }
        return mapper.deleteByPrimaryKey(obj);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int batchDelete(IRequest request, List<MessageEmailWhiteList> objs) {
        int result = 0;
        if (objs == null || objs.isEmpty()) {
            return result;
        }

        for (MessageEmailWhiteList obj : objs) {
            self().deleteMessageEmailWhiteList(request, obj);
            result++;
        }
        return result;
    }

    @Override
    public List<MessageEmailWhiteList> selectMessageEmailWhiteLists(IRequest request, MessageEmailWhiteList example, int page,
            int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<MessageEmailWhiteList> list = mapper.select(example);
        return list;
    }

}
