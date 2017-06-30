package com.ssm.interfaces.mq;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

import com.ssm.account.dto.User;
import com.ssm.activeMQ.annotation.Topic;
import com.ssm.activeMQ.listener.ITopicListener;

@Component
@Topic(channel = "test.soap.topic")
public class TestMqTopic implements ITopicListener<Object>{
	
	private String[] topic = new String[]{"test.soap.topic"};

	@Override
	public String[] getTopic() {
		return topic;
	}

	@Override
	public void onTopicMessage(Object message, String pattern) {
		System.out.println("收到soap发布消息topic = "+message);
		try {
			if(message instanceof TextMessage){ //接收文本消息     
	            TextMessage m = (TextMessage)message;     
	            System.out.println(m.getText());     
	        }else if(message instanceof MapMessage){ //接收键值对消息     
	            MapMessage m = (MapMessage)message;     
	            System.out.println("队列返回的Map消息: result = "+ m.getObject("result"));     
	            
	        }else if(message instanceof StreamMessage){ //接收流消息     
	            StreamMessage m = (StreamMessage)message;     
	            System.out.println(m.readString());     
	            System.out.println(m.readLong());     
	        }else if(message instanceof BytesMessage){ //接收字节消息     
	            byte[] b = new byte[1024];     
	            int len = -1;     
	            BytesMessage m = (BytesMessage)message;     
	            while((len=m.readBytes(b))!=-1){     
	                System.out.println(new String(b, 0, len));     
	            }     
	        }else if(message instanceof ObjectMessage){ //接收对象消息     
	            ObjectMessage m = (ObjectMessage)message;     
	            User user = (User)m.getObject();     
	            System.out.println(user.getUserName() + " _ " + user.getPassword());     
	        }else{     
	            System.out.println(message);     
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
