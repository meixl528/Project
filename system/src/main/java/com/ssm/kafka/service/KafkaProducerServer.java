package com.ssm.kafka.service;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaProducerServer {

	Map<String, Object> sndMesForTemplate(String topic, Object value, String ifPartition, Integer partitionNum,
			String role) throws JsonProcessingException;

}
