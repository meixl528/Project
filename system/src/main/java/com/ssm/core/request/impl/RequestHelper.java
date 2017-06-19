/*
 * #{copyright}#
 */

package com.ssm.core.request.impl;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.MDC;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.ssm.account.dto.Role;
import com.ssm.core.request.IRequest;
import com.ssm.core.request.IRequestListener;

/**
 * 维护 IRequest 实例.
 * 
 * @author meixl
 */
public final class RequestHelper {
    private static ThreadLocal<IRequest> localRequestContext = new ThreadLocal<>();

    private static IRequestListener requestListener = new DefaultRequestListener();

    public IRequestListener getRequestListener() {
        return requestListener;
    }

    /**
     * requestListener可以更改.
     * 
     * @param requestListener
     *            requestListener
     */

    public void setRequestListener(IRequestListener requestListener) {
        RequestHelper.requestListener = requestListener;
    }

    public static IRequest newEmptyRequest() {
        return requestListener.newInstance();
    }

    /**
     * 设置 IRequest.
     * <p>
     * 不检查是否已经存在实例.(存在的话将被替换)
     * 
     * @param request
     *            新的 IRequest 实例
     */
    public static void setCurrentRequest(IRequest request) {
        localRequestContext.set(request);
    }

    /**
     * 清除当前实例.
     * <p>
     * 理论上优于 setCurrentRequest(null)
     */
    public static void clearCurrentRequest() {
        localRequestContext.remove();
    }

    /**
     * @return 当前session信息.
     */
    public static IRequest getCurrentRequest() {
        return getCurrentRequest(false);
    }

    /**
     * 取得当前线程 IRequest.
     * <p>
     * 
     * @param returnEmptyForNull
     *            是否在没有值的时候返回一个空的实例.<br>
     *            注意,返回的空的实例不会设置为当前实例
     * @return 当前 IRequest 实例,或者一个空的实例
     */
    public static IRequest getCurrentRequest(boolean returnEmptyForNull) {
        IRequest request = localRequestContext.get();
        if (request == null && returnEmptyForNull) {
            return newEmptyRequest();
        }
        return request;
    }

    public static IRequest createServiceRequest(HttpServletRequest httpServletRequest) {
        IRequest requestContext = requestListener.newInstance();
        HttpSession session = httpServletRequest.getSession(false);
        if (session != null) {
            requestContext.setUserId((Long) session.getAttribute(IRequest.FIELD_USER_ID));
            requestContext.setRoleId((Long) session.getAttribute(Role.FIELD_ROLE_ID));
            Locale locale = RequestContextUtils.getLocale(httpServletRequest);
            if (locale != null) {
                requestContext.setLocale(locale.toString());
            }
        }
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap != null) {
            mdcMap.forEach((k, v) -> requestContext.setAttribute(IRequest.MDC_PREFIX.concat(k), v));
        }
        requestListener.afterInitialize(httpServletRequest, requestContext);
        
        localRequestContext.set(requestContext);
        return requestContext;
    }
}
