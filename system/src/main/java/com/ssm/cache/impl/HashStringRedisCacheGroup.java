/*
 * #{copyright}#
 */
package com.ssm.cache.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.cache.CacheGroup;

/**
 * @param <T>
 *            元素类型
 * @author meixl
 */
public class HashStringRedisCacheGroup<T> extends RedisCache<T> implements CacheGroup<T> {
	
    private Logger logger = LoggerFactory.getLogger(HashStringRedisCacheGroup.class);

    private ObjectMapper objectMapper;

    private String[] groupField;

    public String[] getGroupField() {
        return groupField;
    }

    public void setGroupField(String[] groupField) {
        this.groupField = groupField;
    }

    private Map<String, HashStringRedisCache<T>> groupMap = new HashMap<>();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected String getGroupValue(Object bean) {
        String key = null;
        try {
            key = getKeyOfBean(bean, groupField);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
        }
        return key;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public T getValue(String key) {
        return null;
    }

    @Override
    public void setValue(String key, T value) {
        String group = getGroupValue(value);
        HashStringRedisCache cache = getGroup(group);
        cache.setValue(key, value);
    }

    public List<T> getGroupAll(String group) {
        return getGroup(group).getAll();
    }

    @Override
    public void remove(String key) {
    }

    public HashStringRedisCache getGroup(String group) {
        HashStringRedisCache hashStringRedisCache = groupMap.get(group);
        if (hashStringRedisCache == null) {
            hashStringRedisCache = new HashStringRedisCache();
            hashStringRedisCache.setRedisTemplate(getRedisTemplate());
            hashStringRedisCache.setObjectMapper(objectMapper);
            hashStringRedisCache.setName(getName() + ":" + group);
            hashStringRedisCache.setCategory(getCategory());
            hashStringRedisCache.setKeyField(getKeyField());
            hashStringRedisCache.setType(getType());
            hashStringRedisCache.init();
            groupMap.put(group, hashStringRedisCache);
        }
        return hashStringRedisCache;
    }

    @Override
    public T getValue(String group, String key) {
        return (T) getGroup(group).getValue(key);
    }

    @Override
    public void setValue(String group, String key, Object value) {
        getGroup(group).setValue(key, value);
    }

    @Override
    public void remove(String group, String key) {
        getGroup(group).remove(key);
    }

    @Override
    public void removeGroup(String group) {
        getGroup(group).clear();
    }

    @Override
    protected void handleRow(Object row) {
        String group = getGroupValue(row);
        HashStringRedisCache<T> cache = getGroup(group);
        cache.handleRow(row);
    }

    @Override
    public void clear() {
        super.clear();
        groupMap.forEach((k, v) -> {
            v.clear();
        });
    }
}
