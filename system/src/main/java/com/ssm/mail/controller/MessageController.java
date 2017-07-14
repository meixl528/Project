package com.ssm.mail.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.core.request.IRequest;
import com.ssm.mail.PriorityLevelEnum;
import com.ssm.mail.ReceiverTypeEnum;
import com.ssm.mail.dto.Message;
import com.ssm.mail.dto.MessageReceiver;
import com.ssm.mail.dto.MessageTransaction;
import com.ssm.mail.mapper.MessageTransactionMapper;
import com.ssm.mail.service.IMessageService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 对消息的操作.
 */
@Controller
public class MessageController extends BaseController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    MessageTransactionMapper messageTransactionMapper;
    /**
     * 查询消息.
     * 
     * @param request
     *            HttpServletRequest
     * @param message
     *            Message
     * @param page
     *            page
     * @param pagesize
     *            pagesize
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/message/query")
    @ResponseBody
    public ResponseData getMessageBySubject(HttpServletRequest request, Message message,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(messageService.selectMessagesBySubject(requestContext, message, page, pagesize));
    }

    /**
     * 查询消息地址.
     * 
     * @param request
     *            HttpServletRequest
     * @param messageReceiver
     *            MessageReceiver
     * @param page
     *            page
     * @param pagesize
     *            pagesize
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/message/queryMessageAddresses")
    @ResponseBody
    public ResponseData getMessageAddressesByMessageId(HttpServletRequest request, MessageReceiver messageReceiver,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(
                messageService.selectMessageAddressesByMessageId(requestContext, messageReceiver, page, pagesize));
    }

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/sys/message/sendTest")
    @ResponseBody
    public ResponseData sendTestMessage(HttpServletRequest request, @RequestBody Map<String, Object> param) throws
            Exception {
        IRequest iRequest = createRequestContext(request);
        String str = param.get("receivers").toString();
        String [] receivers =StringUtils.split(str, ";");
        ArrayList<MessageReceiver> receiverList = new ArrayList<>();
        for(String r:receivers) {
            MessageReceiver mr = new MessageReceiver();
            mr.setMessageAddress(r);
            mr.setMessageType(ReceiverTypeEnum.NORMAL.getCode());
            receiverList.add(mr);
        }

        List<Integer> attachments=(List<Integer>)param.get("attachments");
        List<Long> attachment=null;
        if(null!=attachments){
            attachment=new ArrayList<>();
            for(Integer s:attachments){
                attachment.add(Long.valueOf(s));
            }
        }

        if(param.get("mode").equals("custom")){
            messageService.addEmailMessage((Long)iRequest.getUserId(), param.get("accountCode").toString(),
                    param.get("subject").toString(), param.get("content").toString(), PriorityLevelEnum.NORMAL,attachment,receiverList);
        }else{
        	Map<String,Object> map = new HashMap<>();
        	map.put("receiver", "阳光");
        	map.put("content", "8888");
            messageService.addEmailMessage((Long)iRequest.getUserId(),param.get("accountCode").toString(),param.get("templateCode").toString(), map ,attachment,receiverList);
        }

        return new ResponseData();
    }

    @RequestMapping(value = "/message/error_mess")
    @ResponseBody
    public String errorMess(int messageId){
        Long id=new Long((long)messageId);
       MessageTransaction messageTransaction=new MessageTransaction();
        messageTransaction.setMessageId(id);
        List<MessageTransaction> mess=messageTransactionMapper.select(messageTransaction);
        String result=mess.get(mess.size()-1).getTransactionMessage();
         return result;
    }
}