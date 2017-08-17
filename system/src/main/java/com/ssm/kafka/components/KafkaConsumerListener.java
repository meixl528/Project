package com.ssm.kafka.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
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
public class KafkaConsumerListener implements ApplicationListener<ContextRefreshedEvent>{
	
	private List<KafkaMessageListener<?,?>> listeners = new ArrayList<>();

    @SuppressWarnings({ "rawtypes"})
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
        if(applicationContext.getParent()==null){
			Map<String, KafkaMessageListener> beans = applicationContext.getBeansOfType(KafkaMessageListener.class);
            if (beans != null) {
            	beans.forEach((k, v) -> {
            		if(!listeners.contains(v)){
            			v.init();
            			listeners.add(v);
            		}
                });
            }
        }
	}
	
}
