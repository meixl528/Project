package com.ssm.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ssm.extensible.base.IServiceListener;

/**
 * 多语言特性注解.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("rawtypes")
public @interface ServiceListener {
    Class<?> target();
    
	Class<? extends IServiceListener>[] before() default {};

	Class<? extends IServiceListener>[] after() default {};
}