package com.ssm.activeMQ.listener;
/**
 * @param <T>
 *            数据类型
 * @author meixl
 */
public interface IQueueListener<T> {

    String DEFAULT_METHOD_NAME = "onQueueMessage";

    /**
     * @return 队列名称
     */
    String getQueue();

    /**
     *
     * @param message
     *            经过反序列化的数据
     * @param queue
     *            queue name
     */
    void onQueueMessage(T message, String queue);
}