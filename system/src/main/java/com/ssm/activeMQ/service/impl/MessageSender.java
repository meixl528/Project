package com.ssm.activeMQ.service.impl;

import java.util.Map;
import java.util.stream.Stream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.ssm.activeMQ.service.IMessageSender;
import com.ssm.util.SerializeUtil;
/**
 * 消息队列   & 发布/订阅    :
 * 消息发送方法
 * @author meixl
 */
@Component
public class MessageSender implements IMessageSender{
	
	static Logger logger = LoggerFactory.getLogger(MessageSender.class);

    @Autowired
    @Qualifier("queueJmsTemplate")
    private JmsTemplate queueJmsTemplate;
    @Autowired
    @Qualifier("topicJmsTemplate")
    private JmsTemplate topicJmsTemplate;
    
    @Override
    public void sendQueue(Object message,String queue) {
        try {
        	logger.info("发送  消息("+queue+") : " + message);
        	queueJmsTemplate.convertAndSend(queue, message);
        } catch (Exception e) {
        	logger.error("Send Queue "+queue+" :"+message+" error!");
            e.printStackTrace();
        }
    }
    
    @Override
    public void sendTopic(Object message,String...topic){
    	MessageCreator ms = new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				if(message instanceof String){ //接收文本消息     
					TextMessage msg = session.createTextMessage();
					msg.setText((String)message);
					return msg;
    	        }else if(message instanceof Map){ //接收键值对消息    
    	        	MapMessage msg = session.createMapMessage();
    	        	msg.setObject("map", message);
    	        	return msg;
    	        }else if(message instanceof Stream){ //接收流消息     
    	        	StreamMessage msg = session.createStreamMessage();
    	        	msg.writeObject(message);
    	        	return msg;
    	        }else if(message instanceof Byte){ //接收字节消息     
    	        	BytesMessage msg = session.createBytesMessage();
    	        	msg.writeBytes((byte[]) message);
    	        	return msg;
    	        }else if(message instanceof Object){ //接收对象消息     
    	        	ObjectMessage msg= session.createObjectMessage();
    	        	msg.setObject(SerializeUtil.serialize(message));
    	        	return msg;
    	        }else{     
    	        	logger.error("message :"+ message + " is unclear");  
					throw new JMSException("message type is unclear");
    	        }
			}
		};
		for (String top : topic) {
			logger.info("发送topic消息("+top+") : " + message);
			topicJmsTemplate.send(top,ms);
			//Message msg = topicJmsTemplate.sendAndReceive(new ActiveMQTopic(top), ms);
		}
    }

}