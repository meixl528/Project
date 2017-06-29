package com.ssm.extensible.base;

import com.ssm.core.request.IRequest;

/**
 */
public interface IServiceListener<T> {
    void beforeInsert(IRequest request, T record, ServiceListenerChain<T> chain);

    void afterInsert(IRequest request, T record, ServiceListenerChain<T> chain);

    void beforeUpdate(IRequest request, T record, ServiceListenerChain<T> chain);

    void afterUpdate(IRequest request, T record, ServiceListenerChain<T> chain);

    void beforeDelete(IRequest request, T record, ServiceListenerChain<T> chain);

    void afterDelete(IRequest request, T record, ServiceListenerChain<T> chain);
}
