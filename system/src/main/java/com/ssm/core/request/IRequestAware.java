/*
 * #{copyright}#
 */

package com.ssm.core.request;

/**
 * 一般用于 DTO获取 IRequest.
 * <p>
 * @author meixl
 */
public interface IRequestAware {
    /**
     * 设置上下文.
     * 
     * @param request
     *            请求上下文
     */
    void setRequest(IRequest request);
}
