package com.ssm.kafka.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.stereotype.Service;

/**
 * kafka监听器启动
 * 自动监听是否有消息需要消费
 */
@Service
public class KafkaConsumerListener2 implements MessageListener<String, String>, InitializingBean {
    protected final Logger logger = LoggerFactory.getLogger(KafkaConsumerListener2.class);
    
    @Autowired
    private DefaultKafkaConsumerFactory<?, ?> consumerFactory;
    
    /**
     * 监听器自动执行该方法
     *     消费消息
     *     自动提交offset
     *     执行业务代码
     *     （high level api 不提供offset管理，不能指定offset进行消费）
     */
    //@KafkaListener(topics={"orderTopic"})
    public void onMessage(ConsumerRecord<String, String> record) {
        logger.info("=============kafkaConsumer开始消费=============");
        String topic = record.topic();
        String key = record.key();
        String value = record.value();
        long offset = record.offset();
        int partition = record.partition();
        logger.info("------消费2-------topic:"+topic);
        logger.info("------消费2-------value:"+value);
        logger.info("------消费2-------key:"+key);
        logger.info("------消费2-------offset:"+offset);
        logger.info("------消费2-------partition:"+partition);
        logger.info("~~~~~~~~~~~~~kafkaConsumer消费结束~~~~~~~~~~~~~");
    }
    
    private String[] topic = {"orderTopic2"};
    
	@Override
	public void afterPropertiesSet() throws Exception {
		ContainerProperties containerProperties = new ContainerProperties(topic);
		containerProperties.setMessageListener(this);
		
		new KafkaMessageListenerContainer<>(consumerFactory, containerProperties).start();
	}

}
