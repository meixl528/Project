/*
 * #{copyright}#
 */

package com.ssm.core.request;

import javax.servlet.http.HttpServletRequest;

/**
 * IRequestListener.
 * @author meixl
 */
public interface IRequestListener {
    
    /**
     * 负责提供 IRequest 的实例.
     * 
     * @return 实例
     */
    IRequest newInstance();

    /**
     * 标准属性初始化完毕以后,扩展属性初始化在这里.
     *
     * @param httpServletRequest
     *            HttpServletRequest
     * @param request
     *            实例
     */
    void afterInitialize(HttpServletRequest httpServletRequest, IRequest request);
}
