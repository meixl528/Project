package com.ssm.mail.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.attachment.dto.SysFile;
import com.ssm.attachment.service.ISysFileService;
import com.ssm.mail.EmailStatusEnum;
import com.ssm.mail.EnvironmentEnum;
import com.ssm.mail.MailSender;
import com.ssm.mail.ReceiverTypeEnum;
import com.ssm.mail.dto.Message;
import com.ssm.mail.dto.MessageAddress;
import com.ssm.mail.dto.MessageAttachment;
import com.ssm.mail.dto.MessageEmailAccount;
import com.ssm.mail.dto.MessageEmailAccountVo;
import com.ssm.mail.dto.MessageEmailConfig;
import com.ssm.mail.dto.MessageEmailWhiteList;
import com.ssm.mail.dto.MessageReceiver;
import com.ssm.mail.dto.MessageTransaction;
import com.ssm.mail.mapper.MessageAttachmentMapper;
import com.ssm.mail.mapper.MessageEmailAccountMapper;
import com.ssm.mail.mapper.MessageEmailConfigMapper;
import com.ssm.mail.mapper.MessageEmailWhiteListMapper;
import com.ssm.mail.mapper.MessageMapper;
import com.ssm.mail.mapper.MessageReceiverMapper;
import com.ssm.mail.mapper.MessageTransactionMapper;
import com.ssm.mail.service.IEmailService;

/**
 */
@Transactional
@Service
public class EmailServiceImpl implements IEmailService, BeanFactoryAware {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final int dbTryTime = 3;

    private static final int TWENTY = 20;

    private static final int FIFTY = 50;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageAttachmentMapper attachmentMapper;

    @Autowired
    private MessageReceiverMapper receiverMapper;

    @Autowired
    private MessageTransactionMapper transactionMapper;

    @Autowired
    private MessageEmailAccountMapper emailAccountMapper;

    @Autowired
    private MessageEmailWhiteListMapper emailWhiteListMapper;

    @Autowired
    private MessageEmailConfigMapper emailConfigMapper;

    private BeanFactory beanFactory;

    @Autowired
    private ISysFileService sysFileService;

    //@Autowired
    //private IAESClientService aceClientService;

    @Override
    public boolean sendMessages(Map<String, Object> params) throws Exception {
        Integer batch = (Integer) params.get("batch");
        boolean isVipQueue = (boolean) params.get("isVipQueue");

        if (batch == null) {
            batch = TWENTY;
        }
        if (batch == 0) {
            batch = TWENTY;
        }
        PageHelper.startPage(1, batch);

        List<Message> userEmailToSend = null;
        if (isVipQueue) {
            userEmailToSend = messageMapper.selectVipEmailToSend();
        } else {
            userEmailToSend = messageMapper.selectEmailToSend();
        }
        // messageSetPending(userEmailToSend);
        boolean result = sendMessage(userEmailToSend, params);
        return result;
    }

    @Override
    public boolean reSendMessages(List<Message> messages, Map<String, Object> params) throws Exception {
        boolean result = sendMessage(messages, params);
        return result;
    }

    private void messageSetPending(List<Message> messages) {
        for (Message current : messages) {
            current.setSendFlag("P");
            // messageMapper.updateByPrimaryKeySelective(current);
            this.trySaveTransaction(current, null);
        }
    }

    @Override
    public boolean sendMessageByReceiver(Message message, Map<String, Object> params) throws Exception {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        boolean rs = sendMessage(messages, params);
        return rs;
    }

    public boolean sendMessage(List<Message> userEmailToSend, Map<String, Object> params) throws Exception {

        Map<String, MailSender> senders = new HashMap<String, MailSender>();
        /**
         * 发送邮件部分,没有发送出去的 限制一次取100条,防止堆溢出
         */
        int success = 0;

        messageSetPending(userEmailToSend);

        try {
            for (Message currentMessage : userEmailToSend) {
                List<MessageReceiver> receivers = receiverMapper.selectByMessageId(currentMessage.getMessageId());
                List<MessageAttachment> attachments = attachmentMapper.selectByMessageId(currentMessage.getMessageId());

                String messageFrom = currentMessage.getMessageFrom();
                MessageAddress address = MessageAddress.toAddressObject(messageFrom);
                messageFrom = address.getAddress();

                // 获得mailSender,currentMessage.getMessageFrom()是noReplyAccount的发送邮箱编号

                MailSender mailSender = senders.get(messageFrom);
                if (mailSender == null) {
                    mailSender = (MailSender) beanFactory.getBean("mailSender");

                    MessageEmailAccount record = new MessageEmailAccount();
                    record.setAccountCode(messageFrom);
                    List<MessageEmailAccountVo> selectMessageEmailAccounts = emailAccountMapper
                            .selectMessageEmailAccounts(record);

                    if (selectMessageEmailAccounts == null || selectMessageEmailAccounts.size() == 0) {
                        error(currentMessage, "email account is no more exists:" + messageFrom);
                        continue;
                    }
                    MessageEmailAccountVo mailAccount = selectMessageEmailAccounts.get(0);

                    // email账号被删除的情况
                    if (mailAccount == null) {
                        error(currentMessage, "email account is no more exists:" + messageFrom);
                        continue;
                    }

                    MessageEmailConfig config = emailConfigMapper.selectByPrimaryKey(mailAccount.getConfigId());
                    
                    /**
                     * 不设置此内容邮件发送失败, 503
                     */
                    Properties javaMailProperties = new Properties();
            		javaMailProperties.put("mail.smtp.auth", true);
            		javaMailProperties.put("mail.smtp.starttls.enable", true);
            		mailSender.setJavaMailProperties(javaMailProperties);
                    
                    mailSender.setHost(config.getHost());
                    mailSender.setPort(Integer.parseInt(config.getPort()));
                    if (config.getTryTimes() != null) {
                        mailSender.setTryTimes(config.getTryTimes().intValue());
                    }
                    mailSender.setMessageAccount(mailAccount.getUserName());
                    mailSender.setUsername(config.getUserName());
                    // 是否需要密码
                    if (config.getPassword() != null) {
                        mailSender.setPassword(config.getPassword());
                        // FIXME 不再加密密码
                        // mailSender.setPassword(aceClientService.decrypt(config.getPassword()));
                    }

                    // 白名单
                    if (config.getUseWhiteList() != null && config.getUseWhiteList().equalsIgnoreCase("Y")) {
                        List<MessageEmailWhiteList> whitelist = emailWhiteListMapper
                                .selectByConfigId(config.getConfigId());
                        List<String> stringList = new ArrayList<String>();
                        for (MessageEmailWhiteList current : whitelist) {
                            stringList.add(current.getAddress());
                        }
                        mailSender.setWhiteList(stringList);
                    }
                    senders.put(messageFrom, mailSender);
                }

                // 生成邮件
                // -------------------------------------------------------------------------------------
                MessageTransaction obj = new MessageTransaction();
                Date time = new Date();
                obj.setCreatedBy(-1L);
                obj.setLastUpdatedBy(-1L);
                obj.setCreationDate(time);
                obj.setLastUpdateDate(time);
                obj.setMessageId(currentMessage.getMessageId());
                obj.setObjectVersionNumber(0L);

                if (StringUtils.isAnyBlank(currentMessage.getSubject(), currentMessage.getContent())) {
                    obj.setTransactionStatus(EmailStatusEnum.ERROR.getCode());
                    obj.setTransactionMessage("title or content is null");
                    this.trySaveTransaction(null, obj);
                    continue;
                }

                // 创建多元化邮件
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                // 创建 mimeMessage 帮助类，用于封装信息至 mimeMessage
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                // 发送者地址，必须填写正确的邮件格式，否者会发送失败
                helper.setFrom(mailSender.getMessageAccount());
                // 邮件主题
                helper.setSubject(this.setEmailSubject(mailSender, currentMessage.getSubject()));
                // 邮件内容
                helper.setText(currentMessage.getContent(), true);

                // 附件
                if (attachments != null && attachments.size() > 0) {
                    List<String> attachmentIds = new ArrayList<String>();
                    for (MessageAttachment attachment : attachments) {
                        attachmentIds.add(attachment.getFileId().toString());
                    }
                    List<SysFile> fileNames = sysFileService.selectByIds(null, attachmentIds);
                    for (SysFile sysFile : fileNames) {
                        File file = new File(sysFile.getFilePath());
                        if (file.isFile()) {
                            helper.addAttachment(FilenameUtils.getName(sysFile.getFileName()), file);
                        }
                    }
                }

                for (MessageReceiver receiver : receivers) {
                    if (receiver.getMessageAddress() == null) {
                        continue;
                    }
                    // 白名单过滤
                    if (mailSender.getWhiteList() != null
                            && !mailSender.getWhiteList().contains(receiver.getMessageAddress())) {
                        continue;
                    }
                    if (ReceiverTypeEnum.NORMAL.getCode().equals(receiver.getMessageType())) {
                        // 收件人
                        helper.addTo(receiver.getMessageAddress());
                    } else if (ReceiverTypeEnum.CC.getCode().equals(receiver.getMessageType())) {
                        // 抄送
                        helper.addCc(receiver.getMessageAddress());
                    } else if (ReceiverTypeEnum.BCC.getCode().equals(receiver.getMessageType())) {
                        // 密送
                        helper.addBcc(receiver.getMessageAddress());
                    }
                }
                // -------------------------------------------------------------------------------------

                // 失败,重试次数
                for (int i = 0; i < mailSender.getTryTimes(); i++) {
                    try {
                        mailSender.send(mimeMessage);
                        success++;
                        if (log.isDebugEnabled()) {
                            log.debug("Send mail success, {}.", i);
                        }
                        obj.setTransactionStatus(EmailStatusEnum.SUCCESS.getCode());
                        currentMessage.setSendFlag("Y");
                        this.trySaveTransaction(currentMessage, obj);
                        break;
                    } catch (Exception e) {
                        if (i == mailSender.getTryTimes() - 1) {
                            obj.setTransactionMessage(getExceptionStack(e));
                            obj.setTransactionStatus(EmailStatusEnum.ERROR.getCode());
                            currentMessage.setSendFlag("F");// 此处改成F表示永久失败
                            this.trySaveTransaction(currentMessage, obj);
                            if (log.isErrorEnabled()) {
                                log.error("Send mail failed.", e);
                            }
                        } else {
                            Thread.sleep(FIFTY);
                        }
                    }
                }
            }
        } catch (Exception e) {
            params.put("ERROR_MESSAGE", e.getMessage());
            for (Message message : userEmailToSend) {
                error(message, e.getMessage());
            }
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            throw e;
        }
        prepareSummary(userEmailToSend, params, success);
        return true;
    }

    public void error(Message currentMessage, String msg) {
        MessageTransaction obj = new MessageTransaction();
        Date time = new Date();
        obj.setCreatedBy(-1L);
        obj.setLastUpdatedBy(-1L);
        obj.setCreationDate(time);
        obj.setLastUpdateDate(time);
        obj.setMessageId(currentMessage.getMessageId());
        obj.setObjectVersionNumber(0L);

        obj.setTransactionStatus(EmailStatusEnum.ERROR.getCode());
        obj.setTransactionMessage(msg);
        currentMessage.setSendFlag("F");
        this.trySaveTransaction(currentMessage, obj);
    }

    /**
     * set summary to param map, SendMessageJob will use it.
     * 
     * @param messages
     *            emails to send
     * @param param
     *            execution param
     */
    private void prepareSummary(List<Message> messages, Map<String, Object> param, int success) {
        StringBuilder sb = new StringBuilder();
        if (messages.isEmpty()) {
            sb.append("No Email To Send.");
        } else {
            sb.append("Send ").append(messages.size()).append(" Emails. ");
            sb.append("  Success : " + success);
            Object object = param.get("ERROR_MESSAGE");
            if (object != null) {
                sb.append("  Error :  " + object);
            }
        }
        param.put("summary", sb.toString());
    }

    private void trySaveTransaction(Message message, MessageTransaction obj) {
        for (int i = 0; i < dbTryTime; i++) {
            try {
                self().saveTransaction(message, obj);
                return;
            } catch (Exception e) {
                if (i == dbTryTime - 1) {
                    if (log.isErrorEnabled()) {
                        log.error("save transaction failed.", e);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void saveTransaction(Message message, MessageTransaction obj) {
        if (message != null) {
            messageMapper.updateByPrimaryKeySelective(message);
        }
        if (obj != null) {
            transactionMapper.insertSelective(obj);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * 当发出邮箱带有sit,uat等标记的时候,发出邮件主题上加入[sit],[uat]等标记.
     * 
     * @param mailSender
     * @param subject
     * @return
     */
    private String setEmailSubject(MailSender mailSender, String subject) {
        if ((EnvironmentEnum.SIT.getCode().equals(mailSender.getEnvironment())
                || EnvironmentEnum.UAT.getCode().equals(mailSender.getEnvironment()))) {
            return "[" + mailSender.getEnvironment() + "] " + subject;
        }
        return subject;
    }

    private String getExceptionStack(Throwable th) {
        return ExceptionUtils.getStackTrace(th);
    }
}
