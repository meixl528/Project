package com.ssm.core.jndi.db;

import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 数据源切换AOP切面指定注解拦截处理类
 * @name DynamicDataSourceAspect
 * @description 需要配置  {@code <aop:aspectj-autoproxy />} 以支持@Aspect风格
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
	
	// 轮询计数,初始为-1,AtomicInteger是线程安全的  
    private AtomicInteger counter = new AtomicInteger(-1); 
	
	@Around("@annotation(com.ssm.core.jndi.db.ReadDataSource)")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		//随机
		//int db = (int)(Math.random() * DynamicDataSourceHolder.DATA_SOURCE_RO.length);
		
		//轮询算法实现,得到的下标为：0、1、2、3……  
        Integer db = counter.incrementAndGet() % DynamicDataSourceHolder.DATA_SOURCE_RO.length;  
        if (counter.get() > 999) { // 以免超出Integer范围  
            counter.set(-1); // 还原  
        }
		
		DynamicDataSourceHolder.setDataSourceType(DynamicDataSourceHolder.DATA_SOURCE_RO[db]);
		Object object = pjp.proceed();
		DynamicDataSourceHolder.clearDataSourceType();
		return object;
	}

}
