package com.ssm.interfaces.mq.components;

import org.springframework.stereotype.Service;

import com.ssm.activeMQ.annotation.Queue;
import com.ssm.activeMQ.listener.IQueueListener;

//@Component
@Service
@Queue(queue="rest:queue:test")
public class TestMqQueue implements IQueueListener<Object>{
	
	private String queue = "rest:queue:test";

	@Override
	public String getQueue() {
		return queue;
	}

	@Override
	public void onQueueMessage(Object message, String queue) {
		System.out.println("收到rest发布消息queue = "+message+" and queue = "+ queue);
	}

}
