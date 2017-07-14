package com.ssm.mail.service.impl;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.core.exception.EmailException;
import com.ssm.core.request.IRequest;
import com.ssm.mail.MessageTypeEnum;
import com.ssm.mail.PriorityLevelEnum;
import com.ssm.mail.dto.Message;
import com.ssm.mail.dto.MessageAccount;
import com.ssm.mail.dto.MessageAttachment;
import com.ssm.mail.dto.MessageEmailAccount;
import com.ssm.mail.dto.MessageEmailAccountVo;
import com.ssm.mail.dto.MessageReceiver;
import com.ssm.mail.dto.MessageTemplate;
import com.ssm.mail.mapper.MessageAccountMapper;
import com.ssm.mail.mapper.MessageAttachmentMapper;
import com.ssm.mail.mapper.MessageEmailAccountMapper;
import com.ssm.mail.mapper.MessageMapper;
import com.ssm.mail.mapper.MessageReceiverMapper;
import com.ssm.mail.mapper.MessageTemplateMapper;
import com.ssm.mail.mapper.MessageTransactionMapper;
import com.ssm.mail.service.IMessageService;
import com.ssm.sys.dto.BaseDTO;
import com.ssm.sys.service.IProfileService;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 */
@Transactional
@Service
public class MessageServiceImpl implements IMessageService {

    // 没有收件人不能编辑修改
    private static final String MSG_NO_MESSAGE_RECEIVER = "msg.warning.system.no_message_receiver";

    // 没有该邮件模板
    public static final String MSG_NO_MESSAGE_TEMPLATE = "msg.warning.system.no_message_template";

    // 邮件已经发出不能修改
    private static final String MSG_MESSAGE_HAS_SEND = "msg.warning.system.message_has_send";

    // 信息不全
    private static final String MSG_MESSAGE_TYPE_EMPTY = "msg.warning.system.email_message_type_empty";

    // 没有设置优先级
    private static final String MSG_MESSAGE_PRIORITY_EMPTY = "msg.warning.system.email_message_priority_empty";

    @Autowired
    private IProfileService profileService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageAttachmentMapper attachmentMapper;

    @Autowired
    private MessageReceiverMapper receiverMapper;

    @Autowired
    private MessageTransactionMapper transactionMapper;

    @Autowired
    private MessageTemplateMapper templateMapper;

    @Autowired
    private MessageAccountMapper accountMapper;

    @Autowired
    private MessageEmailAccountMapper emailAccountMapper;

    @Autowired
    private IMessageSender messageSender;

    @Override
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public Message addMessage(Long userId, String templateCode, Map<String, Object> data, List<Long> attachmentIds,
                              List<MessageReceiver> receivers) throws Exception {
        if (receivers == null || receivers.size() == 0) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        MessageTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            // 没有该模板
            throw new EmailException(MSG_NO_MESSAGE_TEMPLATE);
        }
        if (template.getTemplateType() == null) {
            // 没有消息类型
            throw new EmailException(MSG_MESSAGE_TYPE_EMPTY);
        }
        if (template.getPriorityLevel() == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        Date now = new Date();
        MessageAccount account = accountMapper.selectByPrimaryKey(template.getAccountId());
        Message message = new Message();
        message.setMessageType(template.getTemplateType());
        message.setPriorityLevel(template.getPriorityLevel());
        message.setSubject(translateData(template.getSubject(), data));
        message.setContent(translateData(template.getContent(), data));
        message.setSendFlag("N");
        message.setMessageFrom(account.getAccountCode());
        initStd(message, userId, now);

        messageMapper.insertSelective(message);
        // 附件
        if (attachmentIds != null && attachmentIds.size() > 0) {
            for (Long current : attachmentIds) {
                MessageAttachment attachment = new MessageAttachment();
                attachment.setFileId(current);
                attachment.setMessageId(message.getMessageId());
                initStd(attachment, userId, now);

                attachmentMapper.insertSelective(attachment);
            }
        }
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, userId, now);

            receiverMapper.insertSelective(current);
        }
        return message;
    }

    /**
     * 转换模板数据[freemarker].
     * 
     * @param content
     * @param data
     * @return
     * @throws Exception
     */
    private String translateData(String content, Map data) throws Exception {
        if (content == null) {
            return "";
        }
        try (StringWriter out = new StringWriter()) {
            Configuration config = new Configuration();
            config.setDefaultEncoding("utf-8");
            // classic compatible,是${abc}允许出现空值的
            config.setClassicCompatible(true);
            Template template = new Template(null, content, config);
            template.process(data, out);
            return out.toString();
        }
    }

    @Override
    public List<Message> selectMessages(Message message, int page, int pagesize) throws Exception {
        PageHelper.startPage(page, pagesize);
        return messageMapper.select(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Message message) throws Exception {
        transactionMapper.deleteByMessageId(message.getMessageId());
        attachmentMapper.deleteByMessageId(message.getMessageId());
        receiverMapper.deleteByMessageId(message.getMessageId());
        return messageMapper.deleteByPrimaryKey(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Message> messages) throws Exception {
        for (Message message : messages) {
            transactionMapper.deleteByMessageId(message.getMessageId());
            attachmentMapper.deleteByMessageId(message.getMessageId());
            receiverMapper.deleteByMessageId(message.getMessageId());
            messageMapper.deleteByPrimaryKey(message);
        }
    }

    @Override
    public List<Message> selectMessagesBySubject(IRequest requestContext, Message message, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return messageMapper.selectMessagesBySubject(message);
    }

    @Override
    public List<MessageReceiver> selectMessageAddressesByMessageId(IRequest requestContext,
            MessageReceiver messageReceiver, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return receiverMapper.selectMessageAddressesByMessageId(messageReceiver);
    }

    private void initStd(BaseDTO dto, Long userId, Date now) {
        dto.setObjectVersionNumber(1L);
        dto.setCreatedBy(userId == null ? -1L : userId);
        dto.setCreationDate(now);
        dto.setLastUpdatedBy(userId == null ? -1L : userId);
        dto.setLastUpdateDate(now);
    }

    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message addEmailMessage(Long userId, String accountCode, String templateCode, Map<String, Object> data,
            List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception {
        if (receivers == null || receivers.size() == 0) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        MessageTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            // 没有该模板
            throw new EmailException(MSG_NO_MESSAGE_TEMPLATE);
        }
        if (template.getPriorityLevel() == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        Date now = new Date();
        MessageEmailAccount account = emailAccountMapper.selectByAccountCode(accountCode);
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.EMAIL.getCode());
        message.setPriorityLevel(template.getPriorityLevel());
        message.setSubject(translateData(template.getSubject(), data));
        message.setContent(translateData(template.getContent(), data));
        message.setSendFlag("N");
        message.setMessageFrom(account.getAccountCode());
        initStd(message, userId, now);

        messageMapper.insertSelective(message);
        // 附件
        if (attachmentIds != null && attachmentIds.size() > 0) {
            for (Long current : attachmentIds) {
                MessageAttachment attachment = new MessageAttachment();
                attachment.setMessageId(message.getMessageId());
                attachment.setFileId(current);
                initStd(attachment, userId, now);

                attachmentMapper.insertSelective(attachment);
            }
        }
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, userId, now);

            receiverMapper.insertSelective(current);
        }
        if(template.getPriorityLevel().equals("vip")){
        	messageSender.sendQueue(message, "hap:queue:email:vip");
            //messagePublisher.rPush("hap:queue:email:vip",message);
        }else{
        	messageSender.sendQueue(message, "hap:queue:email:normal");
            //messagePublisher.rPush("hap:queue:email:normal",message);
        }
        return message;
    }

    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message addEmailMessage(Long userId, String accountCode, String subject, String content,
                                   PriorityLevelEnum priority, List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception {
        if (receivers == null || receivers.size() == 0) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        if (priority == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        Date now = new Date();
        MessageEmailAccount account = emailAccountMapper.selectByAccountCode(accountCode);
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.EMAIL.getCode());
        message.setPriorityLevel(priority.getCode());
        message.setSubject(subject);
        message.setContent(content);
        message.setSendFlag("N");
        message.setMessageFrom(account.getAccountCode());
        initStd(message, userId, now);

        messageMapper.insertSelective(message);
        // 附件
        if (attachmentIds != null && attachmentIds.size() > 0) {
            for (Long current : attachmentIds) {
                MessageAttachment attachment = new MessageAttachment();
                attachment.setFileId(current);
                attachment.setMessageId(message.getMessageId());
                initStd(attachment, userId, now);

                attachmentMapper.insertSelective(attachment);
            }
        }
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, userId, now);

            receiverMapper.insertSelective(current);
        }
        if(priority.getCode().equals("vip")){
        	messageSender.sendQueue(message, "hap:queue:email:vip");
            //messagePublisher.rPush("hap:queue:email:vip",message);
        }else{
        	messageSender.sendQueue(message, "hap:queue:email:normal");
            //messagePublisher.rPush("hap:queue:email:normal",message);
        }
        return message;
    }


    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message addSiteMessage(Long userId, String templateCode, Map<String, Object> data,
            List<MessageReceiver> receivers) throws Exception {
        if (receivers == null || receivers.size() == 0) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        MessageTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            // 没有该模板
            throw new EmailException(MSG_NO_MESSAGE_TEMPLATE);
        }
        if (template.getPriorityLevel() == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        Date now = new Date();
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.DSIS.getCode());
        message.setPriorityLevel(template.getPriorityLevel());
        message.setSubject(translateData(template.getSubject(), data));
        message.setContent(translateData(template.getContent(), data));
        message.setSendFlag("N");
        initStd(message, userId, now);

        messageMapper.insertSelective(message);
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, userId, now);

            receiverMapper.insertSelective(current);
        }
        return message;
    }

    @Deprecated
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message addSiteMessage(Long userId, String subject, String content, PriorityLevelEnum priority,
            List<MessageReceiver> receivers) throws Exception {
        if (receivers == null || receivers.size() == 0) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        if (priority == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        Date now = new Date();
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.SITE.getCode());
        message.setPriorityLevel(priority.getCode());
        message.setSubject(subject);
        message.setContent(content);
        message.setSendFlag("N");
        initStd(message, userId, now);

        messageMapper.insertSelective(message);
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, userId, now);

            receiverMapper.insertSelective(current);
        }
        return message;
    }

    private MessageTemplate check(Long sender, Long marketId, String templateCode, String smsAccountCode,
            Map<String, Object> data, List<MessageReceiver> receivers) throws Exception {
        if (receivers == null || receivers.size() == 0) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        // MessageTemplate template = templateMapper.selectByCode(templateCode);

        MessageTemplate record = new MessageTemplate();
        record.setTemplateCode(templateCode);
        List<MessageTemplate> selectMessageTemplates = templateMapper.select(record);

        if (selectMessageTemplates == null || selectMessageTemplates.size() == 0) {
            // 没有该模板
            throw new EmailException(MessageServiceImpl.MSG_NO_MESSAGE_TEMPLATE);
        }
        MessageTemplate template = selectMessageTemplates.get(0);

        if (template == null) {
            throw new EmailException(MessageServiceImpl.MSG_NO_MESSAGE_TEMPLATE);
        }

        if (template.getPriorityLevel() == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }
        return template;
    }

    private void check(Long sender, Long marketId, String smsAccountCode, String subject, String content,
            PriorityLevelEnum priority, List<MessageReceiver> receivers) throws Exception {
        if (receivers == null || receivers.size() == 0) {
            // 没有设置收件人,报错
            throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
        }
        if (priority == null) {
            // 没有设置优先级
            throw new EmailException(MSG_MESSAGE_PRIORITY_EMPTY);
        }

        for (MessageReceiver messageReceiver : receivers) {
            String messageAddress = messageReceiver.getMessageAddress();
            if (messageAddress == null || "".equals(messageAddress.trim())) {
                throw new EmailException(MSG_NO_MESSAGE_RECEIVER);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendEmailMessage(Long sender, Long marketId, String templateCode, String emailAccountCode,
            Map<String, Object> data, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        MessageTemplate template = this.check(sender, marketId, templateCode, emailAccountCode, data, receivers);
        String subject = translateData(template.getSubject(), data);
        String content = translateData(template.getContent(), data);
        String priorityS = template.getPriorityLevel();
        PriorityLevelEnum priority = PriorityLevelEnum.valueOf(priorityS);
        Message sendSmsMessage = this.sendEmailMessage(sender, marketId, emailAccountCode, subject, content, priority,
                receivers, attachmentIds);
        return sendSmsMessage;
    }

    public static String from(Long marketId, String accountCode) {

        StringBuilder sb = new StringBuilder();
        sb.append("I");
        sb.append(marketId);
        sb.append("://");
        sb.append(accountCode);
        return sb.toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendEmailMessage(Long sender, Long marketId, String emailAccountCode, String subject, String content,
            PriorityLevelEnum priority, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception {
        this.check(sender, marketId, emailAccountCode, subject, content, priority, receivers);

        Date now = new Date();
        MessageEmailAccount record = new MessageEmailAccount();
        record.setAccountCode(emailAccountCode);
        List<MessageEmailAccountVo> selectMessageEmailAccounts = emailAccountMapper.selectMessageEmailAccounts(record);

        if (selectMessageEmailAccounts == null || selectMessageEmailAccounts.size() == 0) {
            // 没有设置email Account
            throw new EmailException("msg.warning.system.sms_message.email_account.empty");
        }
        MessageEmailAccountVo account = selectMessageEmailAccounts.get(0);
        if (account == null) {
            // 没有设置email Account
            throw new EmailException("msg.warning.system.sms_message.email_account.empty");
        }
        Message message = new Message();
        message.setMessageType(MessageTypeEnum.EMAIL.getCode());
        message.setPriorityLevel(priority.getCode());
        message.setSubject(subject);
        message.setContent(content);
        message.setSendFlag("N");
        // message.setMessageFrom(account.getAccountCode());
        message.setMessageFrom(from(marketId, account.getAccountCode()));
        initStd(message, sender, now);

        messageMapper.insertSelective(message);

        // 附件
        if (attachmentIds != null && attachmentIds.size() > 0) {
            for (Long current : attachmentIds) {
                MessageAttachment attachment = new MessageAttachment();
                // attachment.setAttachmentId(current);
                attachment.setMessageId(message.getMessageId());
                attachment.setFileId(current);
                initStd(attachment, sender, now);

                attachmentMapper.insertSelective(attachment);
            }
        }
        // 收件人(抄送/接收人)
        for (MessageReceiver current : receivers) {
            current.setMessageId(message.getMessageId());
            initStd(current, sender, now);

            receiverMapper.insertSelective(current);
        }
        return message;
    }

}