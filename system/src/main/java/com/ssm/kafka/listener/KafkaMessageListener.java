package com.ssm.kafka.listener;

import org.springframework.kafka.listener.MessageListener;

public interface KafkaMessageListener<K, V> extends MessageListener<K, V>{

	void init();
	
}
