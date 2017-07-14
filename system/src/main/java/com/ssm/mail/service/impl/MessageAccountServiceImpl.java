package com.ssm.mail.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageAccount;
import com.ssm.mail.mapper.MessageAccountMapper;
import com.ssm.mail.service.IMessageAccountService;

/**
 * 消息模板impl.
 */
@Transactional
@Service
public class MessageAccountServiceImpl implements IMessageAccountService, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Autowired
    private MessageAccountMapper accountMapper;

    //@Autowired
    //private IAESClientService aceClientService;
    
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageAccount createMessageAccount(IRequest request, MessageAccount obj) {
        if (obj == null) {
            return null;
        }
        // aes加密
//        AESEncryptors encryptor = (AESEncryptors) beanFactory.getBean("aesEncryptor");
        obj.setPassword(obj.getPassword());
        accountMapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageAccount updateMessageAccount(IRequest request, MessageAccount obj) {
        if (obj == null) {
            return null;
        }
        // 不处理password
        obj.setPassword(null);
        accountMapper.updateByPrimaryKeySelective(obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageAccount updateMessageAccountPasswordOnly(IRequest request, MessageAccount obj) {
        if (obj == null) {
            return null;
        }
        // aes加密
//        AESEncryptors encryptor = (AESEncryptors) beanFactory.getBean("aesEncryptor");
        obj.setPassword(obj.getPassword());
        accountMapper.updateByPrimaryKeySelective(obj);
        return obj;
    }

    @Override
    public MessageAccount selectMessageAccountById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return accountMapper.selectByPrimaryKey(new BigDecimal(objId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteMessageAccount(IRequest request, MessageAccount obj) {
        if (obj.getAccountId() == null) {
            return 0;
        }
        return accountMapper.deleteByPrimaryKey(obj);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int batchDelete(IRequest request, List<MessageAccount> objs) {
        int result = 0;
        if (objs == null || objs.isEmpty()) {
            return result;
        }

        for (MessageAccount obj : objs) {
            self().deleteMessageAccount(request, obj);
            result++;
        }
        return result;
    }

    @Override
    public List<MessageAccount> selectMessageAccounts(IRequest request, MessageAccount example, int page,
            int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<MessageAccount> list = accountMapper.select(example);
        return list;
    }

    @Override
    public List<MessageAccount> selectMessageAccountPassword(IRequest request, MessageAccount example, int page,
            int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<MessageAccount> list = accountMapper.selectMessageAccountPassword(example);
        return list;
    }

}
