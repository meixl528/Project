package com.ssm.core.jndi.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定从只读数据源查询
 * @name ReadDataSource
 * @description 使用时对应的Service实现类不能加Transaction事务注解
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadDataSource {
}
