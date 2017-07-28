package com.ssm.core.jndi.db;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.ssm.core.jndi.RegisterDB;

/**
 * 动态数据源
 * @name DynamicDataSource
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	
	private RegisterDB dbs;

	public RegisterDB getDbs() {
		return dbs;
	}
	public void setDbs(RegisterDB dbs) {
		this.dbs = dbs;
		super.setTargetDataSources(dbs.getMap());
	}
	
	@Override
    protected Object determineCurrentLookupKey() {
    	// 使用DynamicDataSourceHolder保证线程安全，并且得到当前线程中的数据源key
        return DynamicDataSourceHolder.getDataSourceType();
    }
}

