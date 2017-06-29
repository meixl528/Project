package com.ssm.activeMQ.service;

public interface IMessageSender {

	void sendQueue(Object message, String queue);

	void sendTopic(Object message, String...topic);

}
