package com.ssm.core.jndi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ssm.core.jndi.db.DynamicDataSourceHolder;
/**
 * @name        RegisterDB
 * @description 注册数据源bean
 * @author      meixl
 * @date        2017年7月24日下午3:30:21
 */
@Component
public class RegisterDB implements ApplicationContextAware{
	private String writeDB;
	private String readDB;
	
	public RegisterDB(){}
	public RegisterDB(String writeDB,String readDB){
		this.writeDB = writeDB;
		this.readDB = readDB;
	}
	private ApplicationContext applicationContext;
	private Map<Object,Object> map = new HashMap<>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
		List<String> writeList = registBean(writeDB,"readWriteDataSource");
		DynamicDataSourceHolder.DATA_SOURCE_RW = writeList.toArray(new String[writeList.size()]);
		
		List<String> readList = registBean(readDB,"readOnlyDataSource");
		DynamicDataSourceHolder.DATA_SOURCE_RW = readList.toArray(new String[readList.size()]);
	}
	
	private List<String> registBean(String db,String dataSource){
		List<String> list = new ArrayList<>();
		if(!StringUtils.isEmpty(db)){
			String[] dbs = db.split(",");
			for (int i = 0; i < dbs.length; i++) {
				String beanName = dataSource+(i==0?"":i);
				if(!applicationContext.containsBean(beanName)){
					DefaultListableBeanFactory acf = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
			        
			        JndiObjectFactoryBean jndiBean = new JndiObjectFactoryBean();
			        //BeanDefinition beanDefinition = new ChildBeanDefinition("dataSource");
			        
			        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(jndiBean.getClass());
		            beanDefinitionBuilder.addPropertyValue("jndiName", dbs[i].trim());
			        
		        	acf.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
		        }
				if(map.get(beanName)==null){
					map.put(beanName, applicationContext.getBean(beanName));
				}
				if(!list.contains(beanName)){
					list.add(beanName);
				}
			}
		}
		return list;
	}
	
	public Map<Object, Object> getMap() {
		return map;
	}
	public void setMap(Map<Object, Object> map) {
		this.map = map;
	}
}
