package com.ssm.interfaces.mq.components;

import org.springframework.stereotype.Service;

import com.ssm.activeMQ.annotation.Topic;
import com.ssm.activeMQ.listener.ITopicListener;

//@Component
@Service
@Topic(channel = "soap:topic:test")
public class TestMqTopic implements ITopicListener<String>{
	
	private String[] topic = new String[]{"soap:topic:test"};

	@Override
	public String[] getTopic() {
		return topic;
	}

	@Override
	public void onTopicMessage(String message, String pattern) {
		System.out.println("收到soap发布消息topic = "+message);
	}

}
