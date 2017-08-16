package com.ssm.kafka.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ssm.kafka.listener.KafkaMessageListener;

/**
 * kafka监听器启动
 * 自动监听是否有消息需要消费
 */
@Service
public class KafkaConsumer extends KafkaConsumerAdaptor implements KafkaMessageListener<String,String> {
    protected final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    
    private String[] topic = {"orderTopic"};
    
    /**
     * 新建KafkaMessageListenerContainer
     */
    public void init(){
    	super.init(this.getClass().getSimpleName(),this,topic);
    }
    
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
        logger.info("------消费-------topic:"+topic);
        logger.info("------消费-------value:"+value);
        logger.info("------消费-------key:"+key);
        logger.info("------消费-------offset:"+offset);
        logger.info("------消费-------partition:"+partition);
        logger.info("~~~~~~~~~~~~~kafkaConsumer消费结束~~~~~~~~~~~~~");
    }

}
