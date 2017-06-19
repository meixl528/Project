/*
 * #{copyright}#
 */
package com.ssm.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.ssm.cache.Cache;
import com.ssm.cache.CacheManager;

/**
 * @author meixl
 */
@SuppressWarnings("rawtypes")
public class CacheManagerImpl implements CacheManager,ApplicationListener{

    private HashMap<String, Cache<?>> cacheMap = new HashMap<>();
    private List<Cache<?>> caches;

    public void setCaches(List<Cache<?>> caches) {
        this.caches = caches;
        if (caches != null) {
            for (Cache<?> c : caches) {
                cacheMap.put(c.getName(), c);
                c.init();
            }
        }
    }

    public List<Cache<?>> getCaches() {
        return caches;
    }

    @Override
    public <T> Cache<?> getCache(String name) {
        return cacheMap.get(name);
    }

    @Override
    public void addCache(Cache<?> cache) {
        if (caches == null) {
            caches = new ArrayList<>();
        }
        if (!caches.contains(cache)) {
            caches.add(cache);
        }
        cacheMap.put(cache.getName(), cache);
    }
    
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            Map<String, Cache> cacheBeans = applicationContext.getBeansOfType(Cache.class);
            if (cacheBeans != null) {
                cacheBeans.forEach((k, v) -> {
                    if (!caches.contains(v)) {
                        if (StringUtils.isEmpty(v.getName())) {
                            throw new RuntimeException(v + " cacheName is empty");
                        }
                        addCache(v);
                        v.init();
                    }
                });
            }
        }
	}
}
