package com.ssm.kafka.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Component;

import com.ssm.kafka.listener.KafkaMessageListener;

/**
 * @name        KafkaConsumerAdaptor
 * @description 
 * @author      meixl
 * @date        2017年8月16日下午1:09:52
 * @version
 */
@Component
public class KafkaConsumerAdaptor implements ApplicationListener<ContextRefreshedEvent>{
	
	private static final String GROUP_ID = "group.id";

	@Autowired
    @Qualifier(value="consumerProperties")
    private HashMap<String,Object> consumerProperties;
    
    protected void init(String groupId, KafkaMessageListener<?,?> messageListener, String... topic){
    	ContainerProperties containerProperties = new ContainerProperties(topic);
		containerProperties.setMessageListener(messageListener);
		
		HashMap<String,Object> properties = new HashMap<>(consumerProperties);
    	properties.put(GROUP_ID, groupId);
		
		new KafkaMessageListenerContainer<>(new DefaultKafkaConsumerFactory<>(properties), containerProperties).start();
    }

    @SuppressWarnings("rawtypes")
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
        if(applicationContext.getParent()==null){
			Map<String, KafkaMessageListener> beans = applicationContext.getBeansOfType(KafkaMessageListener.class);
            if (beans != null) {
            	beans.forEach((k, v) -> {
                     v.init();
                });
            }
        }
	}
	
}
