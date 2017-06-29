package com.ssm.activeMQ.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;*/

import javax.jms.Message;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.ssm.activeMQ.annotation.Topic;
import com.ssm.activeMQ.listener.ITopicListener;

/**
 * Tpoic 订阅收取,
 * 收取多个channel 的消息
 * @author meixl
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TopicMessageReceiver implements InitializingBean {

	public TopicMessageReceiver() {}

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Autowired
	@Qualifier("topicJmsTemplate")
	private JmsTemplate topicJmsTemplate;
	private Map<String,List<ITopicListener>> beanMap = new HashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, Object> beanWithAnnotation = applicationContext.getBeansWithAnnotation(Topic.class);
		beanWithAnnotation.forEach((k, v) -> {
			Class clazz = v.getClass();
			Topic tm = (Topic) clazz.getAnnotation(Topic.class);
			if (v instanceof ITopicListener) {
				String[] channels = tm.channel();
				List<ITopicListener> lChannel = new ArrayList<>();
				for (String channel : channels) {
					if(!beanMap.isEmpty() && beanMap.get(channel)!=null && !beanMap.get(channel).isEmpty()){
						lChannel = beanMap.get(channel);
						lChannel.add((ITopicListener) v);
						beanMap.put(channel,lChannel);
					}else{
						lChannel.clear();
						lChannel.add((ITopicListener) v);
						beanMap.put(channel,lChannel);
					}
				}
			}
		});
		if(!beanMap.isEmpty()){
			Iterator<String> it = beanMap.keySet().iterator();
			//executorService = Executors.newFixedThreadPool(beanMap.keySet().size());
			String channel;
			List<ITopicListener> list;
			Task task;
			while(it.hasNext()){
				channel = it.next();
				list = beanMap.get(channel);
				task = new Task(channel,list);
				taskExecutor.execute(task);
				//executorService.execute(task);
			}
		}
		
	}
	
	private class Task extends Thread{
		private List<ITopicListener> beans;
		private String channel;
		Task(String channel,List<ITopicListener> list){
			this.channel = channel;
			this.beans = list;
		}
		
		@Override
		public void run() {
			while(true){
				Message message = topicJmsTemplate.receive(channel);
				if(message==null){
					try {
						Thread.sleep(100);
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				for (ITopicListener bean : beans) {
	            	bean.onTopicMessage(message, channel);
				}
				/*if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
		            String text = textMessage.getText();
		            for (ITopicListener bean : beans) {
		            	bean.onTopicMessage(text, channel);
					}
		        }*/
			}
		}
	}
	

	
}