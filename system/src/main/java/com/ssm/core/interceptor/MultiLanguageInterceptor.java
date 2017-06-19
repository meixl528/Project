package com.ssm.core.interceptor;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ssm.core.ILanguageProvider;
import com.ssm.core.ITableNameProvider;
import com.ssm.core.annotation.MultiLanguage;
import com.ssm.core.annotation.MultiLanguageField;
import com.ssm.core.impl.DefaultTableNameProvider;
import com.ssm.core.request.IRequest;
import com.ssm.core.request.impl.RequestHelper;
import com.ssm.core.util.DTOFieldsUtil;
import com.ssm.sys.dto.BaseDTO;
import com.ssm.sys.dto.Language;
/**
 * @description  拦截器拦截Executor接口的update方法（其实也就是SqlSession的新增，删除，修改操作），所有执行executor的update方法都会被该拦截器拦截到
 * @author meixl	2017年4月10日下午3:41:43
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MultiLanguageInterceptor implements Interceptor{
	Logger logger = LoggerFactory.getLogger(MultiLanguageInterceptor.class);
	
	private ITableNameProvider tableNameProvider = DefaultTableNameProvider.getInstance();
	
	@Autowired
    private ILanguageProvider languageProvider;
	

	//实现拦截逻辑的地方，内部要通过invocation.proceed()显式地推进责任链前进，也就是调用下一个拦截器拦截目标方法
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object target = invocation.getTarget();
		if (target instanceof Executor) {
			MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object domain = invocation.getArgs()[1];
			if(domain instanceof BaseDTO){
				BaseDTO dtoObj = (BaseDTO) domain;
				
				//执行invocation.proceed()后,domain中获得insert后的id值.
				Object obj = invocation.proceed();
				if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT || mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
                    proceedMultiLanguage(dtoObj, invocation, mappedStatement);
                    return obj;
                } else if (mappedStatement.getSqlCommandType() == SqlCommandType.DELETE) {
                    proceedDeleteMultiLanguage(dtoObj, invocation);
                    return obj;
                }
			}
		}
		return invocation.proceed();
	}

	//必须写上
    //用当前这个拦截器生成对目标target的代理，实际是通过Plugin.wrap(target,this) 来完成的，把目标target和拦截器this传给了包装函数
	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
	}

	@Override
	public void setProperties(Properties arg0) {
		// TODO Auto-generated method stub
	}
	
	
	private void proceedMultiLanguage(BaseDTO parameterObject, Invocation invocation, MappedStatement mappedStatement)
            throws IllegalArgumentException, IllegalAccessException, SQLException {
        Class<?> clazz = parameterObject.getClass();
        MultiLanguage multiLanguageTable = clazz.getAnnotation(MultiLanguage.class);
        if (multiLanguageTable == null) {
            return;
        }
        Table table = clazz.getAnnotation(Table.class);
        notNull(table, "annotation @Table not found!");
        String tableName = table.name();
        hasText(tableName, "@Table name not found!");
        
        //获取 tb_xx_tl 的表名,存储多语言类型数据
        tableName = tableNameProvider.getTlTableName(tableName);
        if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT) {
            proceedInsertMultiLanguage(tableName, parameterObject, (Executor) invocation.getTarget());
        } else if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
            if (parameterObject.get__tls().isEmpty()) {
                proceedUpdateMultiLanguage(tableName, parameterObject, (Executor) invocation.getTarget());
            } else {
                proceedUpdateMultiLanguage2(tableName, parameterObject, (Executor) invocation.getTarget());
            }
        }
    }
	
	
	private void proceedDeleteMultiLanguage(BaseDTO parameterObject, Invocation invocation)
            throws IllegalArgumentException, IllegalAccessException, SQLException {
        Class<?> clazz = parameterObject.getClass();
        MultiLanguage multiLanguageTable = clazz.getAnnotation(MultiLanguage.class);
        if (multiLanguageTable == null) {
            return;
        }
        Table table = clazz.getAnnotation(Table.class);
        notNull(table, "annotation @Table not found!");
        String tableName = table.name();
        hasText(tableName, "@Table name not found!");
        tableName = tableNameProvider.getTlTableName(tableName);

        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        
        for (Field f : DTOFieldsUtil.getFieldsWithAnnotation(clazz, Id.class)) {
            f.setAccessible(true);
            Object v = f.get(parameterObject);
            keys.add(DTOFieldsUtil.getColumnName(f) + "=?");
            objs.add(v);
        }
        for (Object pkv : objs) {
            if (pkv == null) {
                // 主键中有 null
                return;
            }
        }
        if (keys.size() > 0) {
            Executor executor = (Executor) invocation.getTarget();
            StringBuilder sql = new StringBuilder("DELETE FROM ");
            sql.append(tableName).append(" WHERE ").append(StringUtils.join(keys, " AND "));
            executeSql(executor.getTransaction().getConnection(), sql.toString(), objs);
        }
    }
	
	
	private void proceedInsertMultiLanguage(String tableName, BaseDTO parameterObject, Executor executor)
            throws IllegalArgumentException, IllegalAccessException, SQLException {

        Class<?> clazz = parameterObject.getClass();
        List<String> keys = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + "(");
        for (Field f : DTOFieldsUtil.getFieldsWithAnnotation(clazz, Id.class)) {
            String columnName = DTOFieldsUtil.getColumnName(f);
            keys.add(columnName);
            placeholders.add("?");
            objs.add(f.get(parameterObject));
        }
        keys.add("LANG");
        placeholders.add("?");
        objs.add(null); // 占位符

        Field[] mlFields = DTOFieldsUtil.getFieldsWithAnnotation(clazz, MultiLanguageField.class);
        for (Field f : mlFields) {
            keys.add(DTOFieldsUtil.getColumnName(f));
            placeholders.add("?");
            Map<String, String> tls = parameterObject.get__tls().get(f.getName());
            if (tls == null) {
                // if multi language value not exists in __tls, then use
                // value on current field
                objs.add(f.get(parameterObject));
                continue;
            }
            objs.add(null); // 占位符
        }
        keys.add("CREATED_BY");
        placeholders.add("" + parameterObject.getCreatedBy());

        keys.add("CREATION_DATE");
        placeholders.add("CURRENT_TIMESTAMP");

        keys.add("LAST_UPDATED_BY");
        placeholders.add("" + parameterObject.getCreatedBy());

        keys.add("LAST_UPDATE_DATE");
        placeholders.add("CURRENT_TIMESTAMP");

        sql.append(StringUtils.join(keys, ","));
        sql.append(") VALUES (").append(StringUtils.join(placeholders, ",")).append(")");

        List<Language> languages = languageProvider.getSupportedLanguages();
        for (Language language : languages) {
            objs.set(objs.size() - mlFields.length - 1, language.getLangCode());
            for (int i = 0; i < mlFields.length; i++) {
                int idx = objs.size() - mlFields.length + i;
                Map<String, String> tls = parameterObject.get__tls().get(mlFields[i].getName());
                if (tls != null) {
                    objs.set(idx, tls.get(language.getLangCode()));
                }
                // 当tls为null时,不设置值(使用field的值,旧模式)
            }
            executeSql(executor.getTransaction().getConnection(), sql.toString(), objs);
        }
    }
	
	private void proceedUpdateMultiLanguage(String tableName, BaseDTO parameterObject, Executor executor)
            throws SQLException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = parameterObject.getClass();
        List<String> sets = new ArrayList<>();
        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (Field field : DTOFieldsUtil.getFieldsWithAnnotation(clazz, MultiLanguageField.class)) {
            Object value = field.get(parameterObject);
            if (value == null) {
                continue;
            }
            sets.add(DTOFieldsUtil.getColumnName(field) + "=?");
            objs.add(value);
        }
        if (sets.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("None multi language field has TL value. skip update.");
            }
            return;
        }

        sets.add("LAST_UPDATED_BY=" + parameterObject.getLastUpdatedBy());
        sets.add("LAST_UPDATE_DATE=CURRENT_TIMESTAMP");

        for (Field field : DTOFieldsUtil.getFieldsWithAnnotation(clazz, Id.class)) {
            keys.add(DTOFieldsUtil.getColumnName(field) + "=?");
            objs.add(field.get(parameterObject));
        }
        keys.add("LANG=?");
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
        objs.add(iRequest.getLocale());

        sql.append(StringUtils.join(sets, ","));
        sql.append(" WHERE ").append(StringUtils.join(keys, " AND "));
        if (logger.isDebugEnabled()) {
            logger.debug("Update TL(Classic):{}", sql.toString());
            logger.debug("Parameters:{}", StringUtils.join(objs, ","));
        }

        Connection connection = executor.getTransaction().getConnection();
        int updateCount = executeSql(connection, sql.toString(), objs);
        if (updateCount < 1) {
            if (logger.isWarnEnabled()) {
                logger.warn("Update TL failed(Classic). update count:" + updateCount);
            }
            doInsertForMissingTlData(tableName, iRequest.getLocale(), parameterObject, connection);
        }
    }

    private void proceedUpdateMultiLanguage2(String tableName, BaseDTO parameterObject, Executor executor)
            throws SQLException, IllegalArgumentException, IllegalAccessException {

        Class<?> clazz = parameterObject.getClass();
        List<String> sets = new ArrayList<>();
        List<String> updateFieldNames = new ArrayList<>();

        List<Object> objs = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (Field field : DTOFieldsUtil.getFieldsWithAnnotation(clazz, MultiLanguageField.class)) {
            Map<String, String> tls = parameterObject.get__tls().get(field.getName());
            if (tls == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("TL value for field '{}' not exists.", field.getName());
                }
                // if tl value not exists in __tls, skip.
                continue;
            }
            sets.add(DTOFieldsUtil.getColumnName(field) + "=?");
            updateFieldNames.add(field.getName());
            objs.add(null); // just a placeholder
        }
        if (sets.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("None multi language field has TL value. skip update.");
                return;
            }
        }

        sets.add("LAST_UPDATED_BY=" + parameterObject.getLastUpdatedBy());
        sets.add("LAST_UPDATE_DATE=CURRENT_TIMESTAMP");

        for (Field field : DTOFieldsUtil.getFieldsWithAnnotation(clazz, Id.class)) {
            keys.add(DTOFieldsUtil.getColumnName(field) + "=?");
            objs.add(field.get(parameterObject));
        }
        keys.add("LANG=?");
        objs.add(null); // just a place holder

        sql.append(StringUtils.join(sets, ","));
        sql.append(" WHERE ").append(StringUtils.join(keys, " AND "));

        Connection connection = executor.getTransaction().getConnection();

        List<Language> languages = languageProvider.getSupportedLanguages();
        for (Language language : languages) {
            // 前面几个参数都是多语言数据,需要每次更新
            for (int i = 0; i < updateFieldNames.size(); i++) {
                Map<String, String> tls = parameterObject.get__tls().get(updateFieldNames.get(i));
                objs.set(i, tls.get(language.getLangCode()));
            }

            // 最后一个参数是语言环境
            objs.set(objs.size() - 1, language.getLangCode());

            if (logger.isDebugEnabled()) {
                logger.debug("Update TL(Batch):{}", sql.toString());
                logger.debug("Parameters:{}", StringUtils.join(objs, ", "));
            }
            int updateCount = executeSql(connection, sql.toString(), objs);
            if (updateCount < 1) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Update TL failed(Batch). update count:{},lang:{}", updateCount,
                            language.getLangCode());
                }
                doInsertForMissingTlData(tableName, language.getLangCode(), parameterObject, connection);
            }
        }
    }
    
    private void doInsertForMissingTlData(String tableName, String lang, BaseDTO parameterObject, Connection connection)
            throws IllegalAccessException, SQLException {

        Class clazz = parameterObject.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");
        List<Object> values = new ArrayList<>();
        int pn = 0;
        for (Field f : DTOFieldsUtil.getFieldsWithAnnotation(clazz, Id.class)) {
            sb.append(DTOFieldsUtil.getColumnName(f)).append(",");
            values.add(f.get(parameterObject));
            pn++;
        }
        sb.append("LANG");
        pn++;
        values.add(lang);
        Map<String, Map<String, String>> tls = parameterObject.get__tls();
        for (Field f : DTOFieldsUtil.getFieldsWithAnnotation(clazz, MultiLanguageField.class)) {
            sb.append(",").append(DTOFieldsUtil.getColumnName(f));
            if (tls != null && tls.get(f.getName()) != null) {
                values.add(tls.get(f.getName()).get(lang));
            } else {
                values.add(f.get(parameterObject));
            }
            pn++;
        }
        sb.append(",CREATED_BY");
        values.add(parameterObject.getCreatedBy());
        sb.append(",CREATION_DATE");
        sb.append(",LAST_UPDATED_BY");
        values.add(parameterObject.getLastUpdatedBy());
        sb.append(",LAST_UPDATE_DATE");

        sb.append(") VALUES (");
        for (int i = 0; i < pn; i++) {
            sb.append("?,");
        }
        sb.append("?");
        sb.append(",CURRENT_TIMESTAMP");
        sb.append(",?");
        sb.append(",CURRENT_TIMESTAMP");
        sb.append(")");

        if (logger.isDebugEnabled()) {
            logger.debug("Insert Missing TL record:" + sb.toString());
            logger.debug("Parameters:" + StringUtils.join(values, ", "));
        }

        executeSql(connection, sb.toString(), values);

    }
	
	protected int executeSql(Connection connection, String sql, List<Object> params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object obj : params) {
                ps.setObject(i++, obj);
            }
            ps.execute();
            return ps.getUpdateCount();
        }
    }

}
