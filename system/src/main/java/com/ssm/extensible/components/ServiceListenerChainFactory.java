package com.ssm.extensible.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ssm.extensible.base.ServiceListenerChain;

/**
 */
@Component
@SuppressWarnings("rawtypes")
public class ServiceListenerChainFactory {

    @Autowired
    private ServiceListenerManager manager;

    Map<Object, ServiceListenerChain> chainCache = new HashMap<>();

    
	public ServiceListenerChain getChain(Object service) {
        ServiceListenerChain chain = chainCache.get(service);
        if (chain == null) {
            chain = new ServiceListenerChain(manager, service);
            chainCache.put(service, chain);
        }

        return chain.copy();
    }
}
