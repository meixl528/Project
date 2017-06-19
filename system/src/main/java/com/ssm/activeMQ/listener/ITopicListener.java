package com.ssm.activeMQ.listener;

/**
 * @param <T>
 *            消息类型
 * @author meixl
 */
public interface ITopicListener<T> {

    String DEFAULT_METHOD_NAME = "onTopicMessage";

    /**
     * 订阅消息类型.
     * <p>
     * 可以订阅多个频道,并且可以使用 * 通配符
     * 
     * @return topics
     */
    String[] getTopic();

    void onTopicMessage(T message, String pattern);
}
