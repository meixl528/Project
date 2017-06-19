package com.ssm.core;
/**
 * 数据多语言 TL 表命名规则.
 *
 * @author meixl
 */
public interface ITableNameProvider {
    /**
     * 根据基表名字,取得 tl 表的名字.
     * @param baseTableName
     *            不允许 null
     * @return 不返回 null
     */
    String getTlTableName(String baseTableName);
}
