package com.ssm.core.jndi.db;

import org.apache.commons.lang.StringUtils;

/**
 * 动态处理数据源控制类
 * @name DynamicDataSourceHolder
 */
public class DynamicDataSourceHolder {

	/**
	 * 读写库
	 */
    public static String[] DATA_SOURCE_RW = {"readWriteDataSource"};
    
    /**
     * 只读库
     */
    public static String[] DATA_SOURCE_RO = {"readOnlyDataSource","readOnlyDataSource1"};
    
    //用ThreadLocal来设置当前线程使用的DataSource
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();
    
    /**
     * 设置数据源类型
     * @param customerType
     */
    public static void setDataSourceType(String customerType) {
        contextHolder.set(customerType);
    }
    
    /**
     * 获取数据源类型
     * @return
     */
    public static String getDataSourceType() {
        String dataSource = contextHolder.get();
        if (StringUtils.isEmpty(dataSource)) {
            return DATA_SOURCE_RW[0];
        }else {
            return dataSource;
        }
    }
    
    /**
     * 清除数据源类型
     */
    public static void clearDataSourceType() {
        contextHolder.remove();
    }
	
}
