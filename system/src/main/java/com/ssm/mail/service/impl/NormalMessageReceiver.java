package com.ssm.mail.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ssm.activeMQ.annotation.Queue;
import com.ssm.activeMQ.listener.IQueueListener;
import com.ssm.mail.dto.Message;
import com.ssm.mail.service.IEmailService;

/**
 */
@Service
@Queue(queue="hap:queue:email:normal")
public class NormalMessageReceiver implements IQueueListener<Message> {
	
	private static final Logger logger = LoggerFactory.getLogger(NormalMessageReceiver.class);
	
	private String queue = "hap:queue:email:normal";
	@Override
	public String getQueue() {
		return queue;
	}
	
    @Autowired
    IEmailService emailService;

    @Override
    public void onQueueMessage(Message message, String queue) {
        Map<String,Object> params=new HashMap<>();
        params.put("batch",0);
        params.put("isVipQueue",false);
        try {
        	emailService.sendMessageByReceiver(message,params);
        } catch (Exception e) {
        	logger.error(queue +" 发送邮件异常 : "+e.getMessage());
            e.printStackTrace();
        }
    }
}
