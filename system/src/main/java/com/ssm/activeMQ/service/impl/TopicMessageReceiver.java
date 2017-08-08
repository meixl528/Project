package com.ssm.activeMQ.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;*/
import java.util.concurrent.ExecutorService;
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

import com.ssm.activeMQ.annotation.Topic;
import com.ssm.activeMQ.listener.ITopicListener;
import com.ssm.util.SerializeUtil;

/**
 * Tpoic 订阅收取,
 * 收取多个channel 的消息
 * @author meixl
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TopicMessageReceiver implements InitializingBean {
	static final Logger logger = LoggerFactory.getLogger(TopicMessageReceiver.class);

	public TopicMessageReceiver() {}

	@Autowired
	private ApplicationContext applicationContext;
	
	/*@Autowired
	@Qualifier("taskExecutor")
	private ThreadPoolTaskExecutor taskExecutor;*/

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
			/**
			 * 使用spring的线程池  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor 出现问题, 不执行多个task
			 */
			ExecutorService executorService = Executors.newFixedThreadPool(beanMap.keySet().size());
			String channel;  List<ITopicListener> list;  Task task;
			while(it.hasNext()){
				channel = it.next();
				list = beanMap.get(channel);
				task = new Task(channel,list);
				executorService.execute(task);
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
				try {
					Message message = topicJmsTemplate.receive(channel);
					if(message==null){
						Thread.sleep(100);
						continue;
					}
					if (message instanceof TextMessage) {  //文本
						foreach(((TextMessage)message).getText(), channel);
					}else if(message instanceof MapMessage || message instanceof ObjectMessage){
						foreach(SerializeUtil.unserialize((byte[])((ObjectMessage)message).getObject()), channel);
					}else if(message instanceof StreamMessage){  //流
						foreach(((StreamMessage)message).readString(), channel);
					}else if(message instanceof BytesMessage){ //接收字节消息     
						foreach(((BytesMessage)message).readByte(), channel);
	    	        }else{
	    	        	logger.error("topic receive message error !");
	    	        }
				} catch (Throwable thr) {
					logger.error("exception occurred while get message from topic [" + channel + "]",thr);
				}
			}
		}
		
		private void foreach(Object message,String channel){
			for (ITopicListener bean : beans) {
            	bean.onTopicMessage(message, channel);
			}
		}
	}

	
}