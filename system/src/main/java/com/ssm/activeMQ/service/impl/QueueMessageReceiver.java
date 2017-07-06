package com.ssm.activeMQ.service.impl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;*/

import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ssm.activeMQ.annotation.Queue;
import com.ssm.activeMQ.listener.IQueueListener;

/**
 * Queue 队列消息收取
 * @author meixl
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class QueueMessageReceiver implements InitializingBean {
	
	static Logger logger = LoggerFactory.getLogger(QueueMessageReceiver.class);
    
	public QueueMessageReceiver(){}
	@Autowired
    private ApplicationContext applicationContext;
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
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
			//executorService = Executors.newFixedThreadPool(listeners.size());
			for (Map<IQueueListener, String> listener : listeners) {
				Task task = new Task(listener);
				taskExecutor.execute(task);
			}
		}
	}
	
	private class Task extends Thread{
		private IQueueListener bean;
		private String queue;
		Task(Map<IQueueListener,String> map){
			this.bean = map.keySet().iterator().next();
			this.queue = map.get(bean);
		}
		
		@Override
		public void run(){
			while(true){
				Message message = queueJmsTemplate.receive(queue);
				if(message==null){
					try {
						Thread.sleep(100);
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				bean.onQueueMessage(message, queue);
				/*if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
				   
				    String text = textMessage.getText();
				    bean.onQueueMessage(text, queue);
				    
				}else if(message instanceof MapMessage || message instanceof ObjectMessage){
					ObjectMessage objectMessage = (ObjectMessage)message;
					
					Object object = objectMessage.getObject();
					bean.onQueueMessage(object, queue);
					
				}else if(message instanceof StreamMessage){
					StreamMessage streamMessage = (StreamMessage)message;
					
					String str =streamMessage.readString();
					bean.onQueueMessage(str, queue);
				}*/
			}
		}
	}
	

}