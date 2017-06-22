package com.ssm.core.interceptor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ssm.audit.mapper.AuditMapper;
import com.ssm.audit.service.IAuditTableNameProvider;
import com.ssm.audit.service.impl.DefaultAuditTableNameProvider;
import com.ssm.core.annotation.AuditEnabled;
import com.ssm.mybatis.entity.EntityField;
import com.ssm.sys.dto.BaseDTO;
import com.ssm.sys.dto.DTOClassInfo;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.update.Update;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class AuditInterceptor implements Interceptor {
    private Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);

    public static final ThreadLocal<String> LOCAL_AUDIT_SESSION = new ThreadLocal<>();

    @Autowired
    private BeanFactory beanFactory;
    private AuditMapper templateMapper;

    @Autowired(required = false)
    private IAuditTableNameProvider auditTableNameProvider = DefaultAuditTableNameProvider.instance;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object param = invocation.getArgs()[1];
        if (!(param instanceof BaseDTO)) {
            return invocation.proceed();
        }

        // only enable for @AuditEnabled
        if (param.getClass().getAnnotation(AuditEnabled.class) == null) {
            return invocation.proceed();
        }

        BaseDTO dto = (BaseDTO) param;

        Object result;
        SqlCommandType type = mappedStatement.getSqlCommandType();

        switch (type) {
            case INSERT:
            case UPDATE:
                result = invocation.proceed();
                doAudit(dto, type.name(), mappedStatement);
                break;
            case DELETE:
                doAudit(dto, type.name(), mappedStatement);
                result = invocation.proceed();
                break;
            default:
                result = invocation.proceed();
                break;
        }
        return result;
    }

    public Map<String, Object> getWhereClause(MappedStatement mappedStatement, Object param, String type) throws JSQLParserException {
        BoundSql boundSql = mappedStatement.getBoundSql(param);
        String whereExpression = getWhereExpression(boundSql.getSql(), type);
        int whereParamNum = getWhereParamNum(whereExpression);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Configuration configuration = mappedStatement.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        String WHERE_CLAUSE = whereExpression;
        Map<String, Object> wps = new HashMap<>();
        if (parameterMappings != null) {
            for (int i = parameterMappings.size() - whereParamNum; i < parameterMappings.size(); ++i) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    String propertyName = parameterMapping.getProperty();
                    Object value;
                    if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (param == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(param.getClass())) {
                        value = param;
                    } else {
                        MetaObject typeHandler = configuration.newMetaObject(param);
                        value = typeHandler.getValue(propertyName);
                    }
                    wps.put(propertyName, value);
                    WHERE_CLAUSE = WHERE_CLAUSE.replaceFirst("\\?", "#{" + propertyName + "}");
                }
            }
            wps.put("WHERE_CLAUSE", WHERE_CLAUSE);
        }
        return wps;
    }

    public int getWhereParamNum(String whereExpression) {
        String[] params = whereExpression.trim().split(" ");
        int count = 0;
        for (int i = 0; i < params.length; i++) {
            if (params[i].equals("?")) {
                count++;
            }
        }
        return count;
    }
    public String getWhereExpression(String sql, String type)
            throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Expression where_expression = null;
        if (type.equals("UPDATE")) {
            Update updateStatement = (Update) statement;
            where_expression = updateStatement.getWhere();
        } else if (type.equals("DELETE")) {
            Delete deleteStatement = (Delete) statement;
            where_expression = deleteStatement.getWhere();
        }
        return where_expression.toString();
    }

    /**
     * create audit record.
     *
     * @param param object to be CRU
     * @throws Exception
     */
    private void doAudit(BaseDTO param, String type, MappedStatement mappedStatement) throws Exception {
        Class clazz = param.getClass();
        Table tbl = checkTable(clazz);
        if (tbl == null) {
            return;
        }
        String tableName = tbl.name();
        Field field;
        EntityField[] ids = DTOClassInfo.getIdFields(clazz);
        if (ids.length == 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not get PrimaryKey(s) of dto class:" + clazz);
            }
            return;
        }
        String asid = LOCAL_AUDIT_SESSION.get();
        if (asid == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("audit session is null, skip.");
            }
            return;
        }
        AuditEnabled auditEnabled = (AuditEnabled) clazz.getAnnotation(AuditEnabled.class);
        String auditTableName = StringUtils.defaultIfEmpty(auditEnabled.auditTable(),
                auditTableNameProvider.getAuditTableName(tableName));

        Map<String, Object> auditParam = new HashMap<>();
        auditParam.put("_baseTableName", tableName);
        auditParam.put("_auditTableName", auditTableName);
        auditParam.put("_auditSessionId", asid);
        auditParam.put("_auditType", type);
        //获取where_clause表达式及占位符实参
        Map<String, Object> wps = getWhereClause(mappedStatement, param, type);
        for (Map.Entry<String, Object> entry : wps.entrySet()) {
            auditParam.put(entry.getKey(), entry.getValue());
        }

        List<String> cols = new ArrayList<>();
        DTOClassInfo.getEntityFields(clazz).forEach((k, v) -> {
            if (!v.isAnnotationPresent(Transient.class)) {
                cols.add(DTOClassInfo.getColumnName(v));
            }
        });

        auditParam.put("_cols", cols);

        try {
            int count = getTemplateMapper().auditInsert(auditParam);
            if (count == 1) {
                if (logger.isDebugEnabled()) {
                    logger.debug("audit result:1, normal.");
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("audit result:{}, abnormal.", count);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("audit error:" + e.getMessage());
            }
            throw e;
        }

    }

    private Table checkTable(Class clazz) {
        Table tbl = (Table) clazz.getAnnotation(Table.class);
        if (tbl == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("@Table not found on dto class:" + clazz);
            }
            return null;
        }
        String tableName = tbl.name();
        if (StringUtils.isBlank(tableName)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Can not get tableName of dto class:" + clazz);
            }
            return null;
        }
        return tbl;
    }

    protected AuditMapper getTemplateMapper() {
        if (templateMapper == null) {
            templateMapper = beanFactory.getBean(AuditMapper.class);
        }
        return templateMapper;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public static String generateAndSetAuditSessionId() {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        LOCAL_AUDIT_SESSION.set(id);
        return id;
    }

    public static void clearAuditSessionId() {
        LOCAL_AUDIT_SESSION.remove();
    }
}