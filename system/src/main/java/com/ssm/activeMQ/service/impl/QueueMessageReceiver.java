package com.ssm.activeMQ.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;*/
import java.util.concurrent.Executors;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;

import com.ssm.activeMQ.annotation.Queue;
import com.ssm.activeMQ.listener.IQueueListener;

/**
 * Queue 队列消息收取
 * @author meixl
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class QueueMessageReceiver implements InitializingBean {
	
	static final Logger logger = LoggerFactory.getLogger(QueueMessageReceiver.class);
    
	public QueueMessageReceiver(){}
	@Autowired
    private ApplicationContext applicationContext;
	
	@Autowired
	@Qualifier("queueJmsTemplate")
    private JmsTemplate queueJmsTemplate;
	private List<Map<IQueueListener,String>> listeners = new ArrayList<>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Object> beanWithAnnotation = applicationContext.getBeansWithAnnotation(Queue.class);
		beanWithAnnotation.forEach((k, v) ->{
			Map<IQueueListener,String> map = new HashMap<>();
			Class clazz = v.getClass();
			Queue qm = (Queue) clazz.getAnnotation(Queue.class);
			if(v instanceof IQueueListener){
				map.put((IQueueListener)v,qm.queue());
				listeners.add(map);
			}
		});
		if(listeners!=null && listeners.size() >0){
			/**
			 * 使用spring的线程池  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor 出现问题, 不执行多个task
			 */
			ExecutorService executorService = Executors.newFixedThreadPool(listeners.size());
			for (Map<IQueueListener, String> listener : listeners) {
				QueueTask task = new QueueTask(listener);
				executorService.execute(task);
			}
		}
	}
	
	private class QueueTask extends Thread{
		private IQueueListener bean;
		private String queue;
		QueueTask(Map<IQueueListener,String> map){
			this.bean = map.keySet().iterator().next();
			this.queue = map.get(bean);
		}
		
		@Override
		public void run(){
			while(true){
				try {
					Message message = queueJmsTemplate.receive(queue);
					if(message==null){
						Thread.sleep(100);
						continue;
					}
					//bean.onQueueMessage(message, queue);
					if (message instanceof TextMessage) {  //文本
					    bean.onQueueMessage(((TextMessage) message).getText(), queue);
					}else if(message instanceof MapMessage || message instanceof ObjectMessage){
						bean.onQueueMessage(((ObjectMessage)message).getObject(), queue);
					}else if(message instanceof StreamMessage){  //流
						bean.onQueueMessage(((StreamMessage)message).readString(), queue);
					}else if(message instanceof BytesMessage){ //接收字节消息     
	    	        	bean.onQueueMessage(((BytesMessage)message).readByte(), queue);
	    	        }else{
	    	        	logger.error("queue receive message error !");
	    	        }
				} catch (Throwable thr) {
                    logger.error("exception occurred while get message from queue [" + queue + "]",thr);
				}
			}
		}
	}
	

}