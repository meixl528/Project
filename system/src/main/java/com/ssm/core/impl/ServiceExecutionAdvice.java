/*
 * #{copyright}#
 */

package com.ssm.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import com.ssm.cache.Cache;
import com.ssm.cache.CacheManager;
import com.ssm.cache.annotation.CacheDelete;
import com.ssm.cache.annotation.CacheSet;
import com.ssm.cache.impl.HashStringRedisCache;
import com.ssm.core.annotation.AuditEntry;
import com.ssm.core.annotation.StdWho;
import com.ssm.core.interceptor.AuditInterceptor;
import com.ssm.core.request.IRequest;
import com.ssm.core.request.IRequestAware;
import com.ssm.core.request.impl.RequestHelper;
import com.ssm.mybatis.entity.EntityField;
import com.ssm.sys.dto.BaseDTO;
import com.ssm.sys.dto.DTOClassInfo;

/**
 * service 方法执行通知.
 * <p>
 * 拦截所有 Service 方法执行,处理与 IRequest 相关的参数.
 * <p>
 * service 方法执行时间统计.
 * <p>
 * 从 request 中 copy MDC property 到 真正的 MDC
 * 
 * @author 
 */
public class ServiceExecutionAdvice implements MethodInterceptor {

    private Logger logger = LoggerFactory.getLogger(ServiceExecutionAdvice.class);

    @Autowired
    private CacheManager cacheManager;

    void initMDC(IRequest request) {
        if (request != null) {
            request.getAttributeMap().forEach((key, v) -> {
                if (key.startsWith(IRequest.MDC_PREFIX)) {
                    String mdcProperty = key.substring(IRequest.MDC_PREFIX.length());
                    MDC.put(mdcProperty, (String) v);
                    // logger.debug("copy mdc:key=" + mdcProperty + ",value=" +
                    // mdcValue);
                }
            });
        }
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {

        Class<?>[] types = method.getParameterTypes();
        int idx = -1;

        for (int i = 0; i < types.length; ++i) {
            if (IRequest.class.isAssignableFrom(types[i])) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            // method has no argument of type IRequest
            return;
        }
        IRequest requestContext = (IRequest) args[idx];
        if (requestContext != null) {
            RequestHelper.setCurrentRequest(requestContext);
            initMDC(requestContext);
            // logger.info("before execute " + method + ", set IRequest to
            // ThreadLocal.");
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("{}'s IRequest argument is null.", method);
            }
            return;
        }
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < types.length; ++i) {
            StdWho who = parameters[i].getAnnotation(StdWho.class);
            if (who != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("enable StdWho for argument {}, type: {}", i, types[i].getName());
                }
            }
            if (args[i] instanceof IRequestAware) {
                ((IRequestAware) args[i]).setRequest(requestContext);
            }
            if (args[i] instanceof BaseDTO) {
                BaseDTO baseDTO = (BaseDTO) args[i];
                autoAssignStdProperty(logger, requestContext, baseDTO, who != null);
            } else if (args[i] instanceof Collection) {
                for (Object o : (Collection) args[i]) {
                    if (args[i] instanceof IRequestAware) {
                        ((IRequestAware) args[i]).setRequest(requestContext);
                    }
                    if (o instanceof BaseDTO) {
                        autoAssignStdProperty(logger, requestContext, (BaseDTO) o, who != null);
                    }
                }
            }
        }
    }

    private void autoAssignStdProperty(Logger logger, IRequest request, BaseDTO dto, boolean stdWhoSupport) {
        if (stdWhoSupport) {
            dto.setCreatedBy(request.getUserId());
            dto.setLastUpdatedBy(request.getUserId());
            dto.setLastUpdateLogin(request.getUserId());
            dto.setCreationDate(new Date());
            dto.setLastUpdateDate(new Date());
        }
        // logger.info("auto assign request property for " + dto);
        for (EntityField field : DTOClassInfo.getChildrenFields(dto.getClass())) {
            if (!checkChildrenType(field.getJavaType())) {
                if (logger.isWarnEnabled()) {
                    logger.warn("property '{}' is annotated by @Children, incorrect usage.", field.getName());
                }
                return;
            }
            try {
                Object p = PropertyUtils.getProperty(dto,field.getName());
                if (p instanceof BaseDTO) {
                    autoAssignStdProperty(logger, request, (BaseDTO) p, stdWhoSupport);
                } else if (p instanceof Collection) {
                    for (Object o : (Collection) p) {
                        if (o instanceof BaseDTO) {
                            autoAssignStdProperty(logger, request, (BaseDTO) o, stdWhoSupport);
                        }
                    }
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 检查一个被@Children 标注的属性的类型,是否被支持.
     * 
     * @param type
     *            class type
     * @return the type is supported or not
     */
    protected boolean checkChildrenType(Class<?> type) {
        if (BaseDTO.class.isAssignableFrom(type)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        Logger serviceLogger = LoggerFactory.getLogger(target.getClass());
        String methodStamp = target + "#" + method.getName();
        if (logger.isTraceEnabled()) {
            serviceLogger.trace("{}, parameters: {}", methodStamp, Arrays.toString(invocation.getArguments()));
        }
        long ts = System.currentTimeMillis();
        AuditEntry entry = null;
        boolean createAuditSessionId = false;
        try {
            before(method, invocation.getArguments(), target);
            entry = AnnotationUtils.findAnnotation(method, AuditEntry.class);
            if (entry != null) {
                String entityCode = entry.value();
                String auditFlag = getAuditFlag(entityCode);
                if ("Y".equalsIgnoreCase(auditFlag)) {
                    if (AuditInterceptor.LOCAL_AUDIT_SESSION.get() == null) {
                        // create audit session id only when there is no one
                        AuditInterceptor.generateAndSetAuditSessionId();
                        createAuditSessionId = true;
                    }
                }
            }
            Object ret = invocation.proceed();
            if (logger.isTraceEnabled()) {
                serviceLogger.trace("{}, return value: {}", methodStamp, ret);
            }
            proceedAutoCacheOperation(invocation, ret);
            return ret;
        } finally {
            if (entry != null && createAuditSessionId) {
                AuditInterceptor.clearAuditSessionId();
            }
            long timeUsed = System.currentTimeMillis() - ts;
            if (logger.isTraceEnabled()) {
                logger.trace("{}, execution time: {}ms.", methodStamp, timeUsed);
            }
        }
    }

    private void proceedAutoCacheOperation(MethodInvocation invocation, Object ret)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        method = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        CacheSet cacheSet = AnnotationUtils.findAnnotation(method, CacheSet.class);
        if (cacheSet != null) {
            proceedCacheSet(method, cacheSet, ret);
        }
        CacheDelete cacheDelete = AnnotationUtils.findAnnotation(method, CacheDelete.class);
        if (cacheDelete != null) {
            proceedCacheDelete(invocation, cacheDelete);
        }
    }

    @SuppressWarnings("unchecked")
    private void proceedCacheSet(Method method, CacheSet cacheSet, Object ret)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String cacheName = cacheSet.cache();
        if (!StringUtils.hasText(cacheName)) {
            throw new IllegalArgumentException("cache: " + cacheName + " is not valid.");
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("cache: {} not found.", cacheName);
            }
            return;
        }
        String key = cache.getCacheKey(ret);
        if (cache instanceof HashStringRedisCache) {
            String valueField = ((HashStringRedisCache) cache).getValueField();
            if (valueField != null) {
                ret = PropertyUtils.getProperty(ret, valueField);
            }
        }
        cache.setValue(key, ret);
        if (logger.isDebugEnabled()) {
            logger.debug("{} cache auto set. key={}", method.getName(), key);
        }
    }

    @SuppressWarnings("unchecked")
    private void proceedCacheDelete(MethodInvocation invocation, CacheDelete cacheDelete) throws NoSuchMethodException {
        String cacheName = cacheDelete.cache();
        if (!StringUtils.hasText(cacheName)) {
            throw new IllegalArgumentException("cache: " + cacheName + " is not valid.");
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("cache: {} not found.", cacheName);
            }
            return;
        }
        Object args0 = invocation.getArguments()[0];
        String key = cache.getCacheKey(args0);
        cache.remove(key);
        if (logger.isDebugEnabled()) {
            logger.debug("{} cache auto remove. key={}", invocation.getMethod().getName(), key);
        }
    }
    private String getAuditFlag (String entityCode) {
       /* String auditFlag = "N";
        Cache cache = cacheManager.getCache("audit");
        if (cache != null) {
            auditFlag = (String) cache.getValue(entityCode);
        }
        return auditFlag;*/
        return "Y";
    }
}
