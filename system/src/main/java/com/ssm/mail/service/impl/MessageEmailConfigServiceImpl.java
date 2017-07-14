package com.ssm.mail.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.core.exception.EmailException;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageEmailAccount;
import com.ssm.mail.dto.MessageEmailConfig;
import com.ssm.mail.dto.MessageEmailWhiteList;
import com.ssm.mail.mapper.MessageEmailAccountMapper;
import com.ssm.mail.mapper.MessageEmailConfigMapper;
import com.ssm.mail.mapper.MessageEmailWhiteListMapper;
import com.ssm.mail.service.IMessageEmailConfigService;

/**
 * 邮箱配置impl.
 */
@Transactional
@Service
public class MessageEmailConfigServiceImpl implements IMessageEmailConfigService, BeanFactoryAware {

    // 选择白名单没有设置名单
    private static final String MSG_MESSAGE_NO_WHITE_LIST = "msg.warning.system.email_message_no_whitelist";
    
    // 账号列表为空
    private static final String MSG_MESSAGE_NO_ACCOUNT_LIST = "msg.warning.system.email_message_on_accountlist";
    
    private BeanFactory beanFactory;

    @Autowired
    private MessageEmailConfigMapper mapper;

    @Autowired
    private MessageEmailAccountMapper accountMapper;
    
    @Autowired
    private MessageEmailWhiteListMapper addressMapper;
    
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
    public MessageEmailConfig createMessageEmailConfig(IRequest request, MessageEmailConfig obj) {
        if (obj == null) {
            return null;
        }
        // aes加密
//        AESEncryptors encryptor = (AESEncryptors) beanFactory.getBean("aesEncryptor");
        obj.setPassword(obj.getPassword());
        mapper.insertSelective(obj);
        return obj;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public MessageEmailConfig updateMessageEmailConfig(IRequest request, MessageEmailConfig obj) {
        if (obj == null) {
            return null;
        }
        MessageEmailConfig config = mapper.selectByPrimaryKey(obj.getConfigId());
        if(config.getPassword() != null && config.getPassword().equals(obj.getPassword())){
            // 没有修改密码
            obj.setPassword(null);
        }else{
            // aes加密
//            AESEncryptors encryptor = (AESEncryptors) beanFactory.getBean("aesEncryptor");
            obj.setPassword(obj.getPassword());
        }
        mapper.updateByPrimaryKeySelective(obj);
        return obj;
    }

    @Override
    public MessageEmailConfig selectMessageEmailConfigById(IRequest request, Long objId) {
        if (objId == null) {
            return null;
        }
        return mapper.selectByPrimaryKey(objId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteMessageEmailConfig(IRequest request, MessageEmailConfig obj) {
        if (obj.getConfigId() == null) {
            return 0;
        }
        int count = 0;
        count += accountMapper.deleteByConfigId(obj.getConfigId());
        count += addressMapper.deleteByConfigId(obj.getConfigId());
        count += mapper.deleteByPrimaryKey(obj);
        return count;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int batchDelete(IRequest request, List<MessageEmailConfig> objs) {
        int result = 0;
        if (objs == null || objs.isEmpty()) {
            return result;
        }

        for (MessageEmailConfig obj : objs) {
            self().deleteMessageEmailConfig(request, obj);
            result++;
        }
        return result;
    }

    @Override
    public List<MessageEmailConfig> selectMessageEmailConfigs(IRequest request, MessageEmailConfig example, int page,
            int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<MessageEmailConfig> list = mapper.selectMessageEmailConfigs(example);
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private MessageEmailAccount createEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj == null) {
            return null;
        }
        accountMapper.insertSelective(obj);
        return obj;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private MessageEmailWhiteList createAddress(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        addressMapper.insertSelective(obj);
        return obj;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private MessageEmailAccount updateEmailAccount(IRequest request, MessageEmailAccount obj) {
        if (obj == null) {
            return null;
        }
        accountMapper.updateByPrimaryKeySelective(obj);
        return obj;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private MessageEmailWhiteList updateAddress(IRequest request, MessageEmailWhiteList obj) {
        if (obj == null) {
            return null;
        }
        addressMapper.updateByPrimaryKeySelective(obj);
        return obj;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void batchUpdate(IRequest requestContext, MessageEmailConfig obj) throws EmailException {
        if(obj != null){
            /* Mclin修改，帐号列表和白名单无论在新建还是更新的时候都需要验证 */
            /*if(obj.getEmailAccounts() == null || obj.getEmailAccounts().size() == 0 ){
                // 账号列表为空
                throw new EmailException(MSG_MESSAGE_NO_ACCOUNT_LIST);
            }*/
            /*if(obj.getUseWhiteList() != null && obj.getUseWhiteList().equalsIgnoreCase("Y") && (obj.getWhiteLists() == null || obj.getWhiteLists().size() == 0) ){
                // 选择白名单没有设置名单
                throw new EmailException(MSG_MESSAGE_NO_WHITE_LIST);
            }*/
            if(obj.getConfigId() == null){
                /*if(obj.getEmailAccounts() == null || obj.getEmailAccounts().size() == 0 ){
                    // 账号列表为空
                    throw new EmailException(MSG_MESSAGE_NO_ACCOUNT_LIST);
                }
                if(obj.getUseWhiteList() != null && obj.getUseWhiteList().equalsIgnoreCase("Y") && (obj.getWhiteLists() == null || obj.getWhiteLists().size() == 0) ){
                    // 选择白名单没有设置名单
                    throw new EmailException(MSG_MESSAGE_NO_WHITE_LIST);
                }*/
                createMessageEmailConfig(requestContext, obj);
            }else{
                updateMessageEmailConfig(requestContext, obj);
            }
            
            if(obj.getEmailAccounts() != null){
                for (MessageEmailAccount current : obj.getEmailAccounts()) {
                    /* Mclin修改，添 验证帐号编号和市场唯一性*/
                    if (validEmailAccount(requestContext, current) == false) {
                        throw new EmailException(EmailException.MSG_ERROR_SAME_CODE_AND_MARKET_ID_IS_EXISTS);
                    }
                    if (current.getAccountId() == null) {
                        // 没意义的值
                        current.setObjectVersionNumber(0L);
                        current.setConfigId(obj.getConfigId());
//                        if(current.getPassword() != null){
//                            // aes加密
//                            AESEncryptors encryptor = (AESEncryptors) beanFactory.getBean("aesEncryptor");
//                            current.setPassword(aceClientService.encrypt(current.getPassword()));
//                        }
                        createEmailAccount(requestContext, current);
                    } else {
//                        if(current.getPassword() != null){
//                            List<MessageEmailAccount> datas = accountMapper.selectMessageEmailAccountPassword(current);
//                            if(datas != null && datas.size() == 1){
//                                if(datas.get(0).getPassword() != null && datas.get(0).getPassword().equals(current.getPassword())){
//                                    //没有修改密码
//                                    current.setPassword(null);
//                                }else{
//                                    // aes加密
//                                    AESEncryptors encryptor = (AESEncryptors) beanFactory.getBean("aesEncryptor");
//                                    current.setPassword(aceClientService.encrypt(current.getPassword()));
//                                }
//                            }
//                        }
                        updateEmailAccount(requestContext, current);
                    }
                }
            }
            if(obj.getWhiteLists() != null){
                for (MessageEmailWhiteList current : obj.getWhiteLists()) {
                    if (current.getId() == null) {
                        //没意义的值
                        current.setObjectVersionNumber(0L);
                        current.setConfigId(obj.getConfigId());
                        createAddress(requestContext, current);
                    } else {
                        updateAddress(requestContext, current);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> queryMsgConfigQuanties(IRequest request) {
        Map<String, Object> map = new HashMap<>();
        Integer msgConfigAmount = mapper.queryMsgConfigQuanties();
        map.put("msgConfigAmount", msgConfigAmount);
        return map;
    }
    
    private boolean validEmailAccount(IRequest request, MessageEmailAccount obj) {
        MessageEmailAccount acc = accountMapper.getMsgEmailAccountByCode(obj.getAccountId(),
                obj.getAccountCode());
        if (acc == null) {
            return true;
        }
        return false;
    }

}
