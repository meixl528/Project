package com.ssm.core.impl;

import com.ssm.core.ITableNameProvider;

/**
 * @description  tb_xx_b 与    tb_xx_tl 表名转换,tl后缀为多语言表
 * @author meixl	2017年4月10日下午4:33:17
 */
public class DefaultTableNameProvider implements ITableNameProvider {

	private static DefaultTableNameProvider instance = new DefaultTableNameProvider();
    private static final String SUFFIX = "_TL";

    public static DefaultTableNameProvider getInstance() {
        return instance;
    }

    private DefaultTableNameProvider() {
    }
	
	@Override
	public String getTlTableName(String baseTableName) {
		int len = baseTableName.length();
        return new StringBuilder(baseTableName).delete(len - 2, len).append(SUFFIX).toString();
	}
	

}
