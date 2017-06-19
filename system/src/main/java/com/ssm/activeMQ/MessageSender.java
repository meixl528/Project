package com.ssm.activeMQ;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
/**
 * 消息队列   & 发布/订阅    :
 * 消息发送方法
 * @author meixl
 */
public class MessageSender{

    @Autowired
    @Qualifier("queueJmsTemplate")
    private JmsTemplate queueJmsTemplate;
    @Autowired
    @Qualifier("topicJmsTemplate")
    private JmsTemplate topicJmsTemplate;
    
    public void sendQueue(String text,String queue) {
        try {
        	System.out.println("发送  消息("+queue+") : " + text);
        	queueJmsTemplate.convertAndSend(queue, text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendTopic(String text,String...topic){
    	MessageCreator ms = new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage();
				// 设置消息属性  
                msg.setStringProperty("msgCode", "C001");  
                // 设置消息内容  
                msg.setText(text);  
                return msg;  
			}
		};
		for (String top : topic) {
			System.out.println("发送topic消息("+top+") : " + text);
			topicJmsTemplate.send(top,ms);
			//Message msg = topicJmsTemplate.sendAndReceive(new ActiveMQTopic(top), ms);
			
		}
    }

}