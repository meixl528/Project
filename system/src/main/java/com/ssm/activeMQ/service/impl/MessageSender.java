package com.ssm.activeMQ.service.impl;

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
        	logger.debug("发送  消息("+queue+") : " + message);
        	queueJmsTemplate.convertAndSend(queue, message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("Send Queue "+queue+" :"+message+" error!");
        }
    }
    
    @Override
    public void sendTopic(Object message,String...topic){
    	MessageCreator ms = new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message msg = session.createMessage();
				// 设置消息属性  
                msg.setStringProperty("Code", "Test");  
				
				if(message instanceof String){ //接收文本消息     
					((TextMessage) msg).setText((String)message);
					
    	        }else if(message instanceof MapMessage){ //接收键值对消息     
    	        	((MapMessage) msg).setObject("map", message);
    	            
    	        }else if(message instanceof StreamMessage){ //接收流消息     
    	        	((StreamMessage) msg).writeObject(message);
    	           
    	        }else if(message instanceof BytesMessage){ //接收字节消息     
    	        	((BytesMessage) msg).writeBytes((byte[]) message);
    	              
    	        }else if(message instanceof ObjectMessage){ //接收对象消息     
    	        	((ObjectMessage) msg).setObject(SerializeUtil.serialize(message));
    	        }else{     
    	        	logger.debug("message :"+ message + " is unclear");  
					throw new JMSException("message type is unclear");
    	        }
				return msg;
			}
		};
		for (String top : topic) {
			logger.debug("发送topic消息("+top+") : " + message);
			topicJmsTemplate.send(top,ms);
			//Message msg = topicJmsTemplate.sendAndReceive(new ActiveMQTopic(top), ms);
		}
    }

}