package com.ssm.kafka.listener;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

import com.ssm.kafka.annotation.KafkaTopic;

public class KafkaConsumerAdaptor {
	
	@Autowired
    @Qualifier(value="consumerProperties")
    private HashMap<String,Object> consumerProperties;
    
	/**
     * 加载KafkaMessageListenerContainer
     */
	public void init(){
    	Map<String,Object> properties = new HashMap<>(consumerProperties);
    	properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.getClass().getSimpleName());
    	
    	ContainerProperties containerProperties = new ContainerProperties(this.getClass().getDeclaredAnnotation(KafkaTopic.class).topics());
		containerProperties.setMessageListener(this);
		
		new KafkaMessageListenerContainer<>(new DefaultKafkaConsumerFactory<>(properties), containerProperties).start();
    }
	
}
