package com.ssm.kafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @name        KafkaTopic
 * @description kafkaTopic注解,指定topics
 * @author      meixl
 * @date        2017年8月17日上午11:05:55
 * @version
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface KafkaTopic {

	String[] topics() default {};
	
}
