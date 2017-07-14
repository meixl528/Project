package com.ssm.mail.service;

import java.util.List;
import java.util.Map;

import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.mail.PriorityLevelEnum;
import com.ssm.mail.dto.Message;
import com.ssm.mail.dto.MessageReceiver;

/**
 * 消息服务,email/sms增删查改.
 */
public interface IMessageService extends ProxySelf<IMessageService> {

    /*
     * 发送信息——先添加到待发送邮件表,后定时发出
     * 
     * attachmentIds是附件表id列表
     * 
     * MessageReceiver里面messageType="normal"代表是普通发送,messageType="copy"代表抄送
     * 发送给3个人(不带抄送)则传入3个normal的接收人list 发送给1个人(带2个人的抄送)则传入1个normal,2个copy的接收人list
     * --------------------------------------------------
     * 
     * 如果templateCode非空,则templateData是填充template的数据,覆盖message内的content
     * 为空则直接使用message内的content
     * -------------------------------------------------- message中的
     * messageFrom是发出邮箱编号,数据库Message_Account表
     *
     */
    Message addMessage(Long userId, String templateCode, Map<String, Object> data, List<Long> attachmentIds,
            List<MessageReceiver> receivers) throws Exception;

    Message addEmailMessage(Long userId, String accountCode, String templateCode, Map<String, Object> data,
            List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception;

    Message addEmailMessage(Long userId, String accountCode, String subject, String content, PriorityLevelEnum priority,
            List<Long> attachmentIds, List<MessageReceiver> receivers) throws Exception;

    Message addSiteMessage(Long userId, String templateCode, Map<String, Object> data, List<MessageReceiver> receivers)
            throws Exception;

    Message addSiteMessage(Long userId, String subject, String content, PriorityLevelEnum priority,
            List<MessageReceiver> receivers) throws Exception;

    /*
     * 查询消息
     * 
     * @param message
     * 
     * @param page
     * 
     * @param pagesize
     * 
     * @return responseData
     */
    List<Message> selectMessages(Message message, int page, int pagesize) throws Exception;

    /*
     * 删除消息
     * 
     * @param message
     * 
     * @return
     */
    int delete(Message message) throws Exception;

    /*
     * 批量删除
     * 
     * @param users
     * 
     * @return
     * 
     * @throws Exception
     */
    void batchDelete(List<Message> messages) throws Exception;

    /*
     * （根据标题）查询消息
     * 
     * @param message
     * 
     * @param page
     * 
     * @param pagesize
     * 
     * @return responseData
     */
    List<Message> selectMessagesBySubject(IRequest requestContext, Message message, int page, int pagesize);

    /*
     * 根据messageId查询消息地址
     * 
     * @param message
     * 
     * @param page
     * 
     * @param pagesize
     * 
     * @return responseData
     */
    List<MessageReceiver> selectMessageAddressesByMessageId(IRequest requestContext, MessageReceiver messageReceiver,
            int page, int pagesize);

    /**
     * @param sender
     *            发送人ID user_id
     * @param marketId
     *            市场ID
     * @param emailAccountCode
     *            发送账号编号
     * @param subject
     *            标题
     * @param content
     *            内容
     * @param priority
     *            优先级
     * @param receivers
     *            接收者
     * @param attachmentIds
     *            附件列表可以为null
     * @return Message
     * @throws Exception
     */
    Message sendEmailMessage(Long sender, Long marketId, String emailAccountCode, String subject, String content,
            PriorityLevelEnum priority, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception;

    /**
     * @param sender
     *            发送人ID user_id
     * @param marketId
     *            市场ID
     * @param templateCode
     *            模版编号
     * @param emailAccountCode
     *            发送账号编号
     * @param data
     *            模版数据
     * @param receivers
     *            接收者
     * @param attachmentIds
     *            附件列表可以为null
     * @return Message
     * @throws Exception
     */
    Message sendEmailMessage(Long sender, Long marketId, String templateCode, String emailAccountCode,
            Map<String, Object> data, List<MessageReceiver> receivers, List<Long> attachmentIds) throws Exception;

}
