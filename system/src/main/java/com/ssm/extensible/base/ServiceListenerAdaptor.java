/*
 * Copyright Hand China Co.,Ltd.
 */

package com.ssm.extensible.base;

import com.ssm.core.request.IRequest;

/**
 */
public class ServiceListenerAdaptor<T> implements IServiceListener<T> {
    @Override
    public void beforeInsert(IRequest request, T record, ServiceListenerChain<T> chain) {

    }

    @Override
    public void afterInsert(IRequest request, T record, ServiceListenerChain<T> chain) {

    }

    @Override
    public void beforeUpdate(IRequest request, T record, ServiceListenerChain<T> chain) {

    }

    @Override
    public void afterUpdate(IRequest request, T record, ServiceListenerChain<T> chain) {

    }

    @Override
    public void beforeDelete(IRequest request, T record, ServiceListenerChain<T> chain) {

    }

    @Override
    public void afterDelete(IRequest request, T record, ServiceListenerChain<T> chain) {

    }
}
