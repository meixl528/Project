package com.ssm.interfaces.mq;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

import com.ssm.account.dto.User;
import com.ssm.activeMQ.annotation.Queue;
import com.ssm.activeMQ.listener.IQueueListener;

@Component
@Queue(queue="test.rest.queue")
public class TestMqQueue implements IQueueListener<Object>{
	
	private String queue = "test.rest.queue";

	@Override
	public String getQueue() {
		return queue;
	}

	@Override
	public void onQueueMessage(Object message, String queue) {
		System.out.println("message = "+message+" and queue = "+ queue);
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
