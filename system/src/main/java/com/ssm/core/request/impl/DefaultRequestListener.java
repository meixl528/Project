/*
 * #{copyright}#
 */

package com.ssm.core.request.impl;

import javax.servlet.http.HttpServletRequest;

import com.ssm.core.request.IRequest;
import com.ssm.core.request.IRequestListener;
/**
 * @author meixl
 */
public class DefaultRequestListener implements IRequestListener {
    
    @Override
    public IRequest newInstance() {
        return new ServiceRequest();
    }

    @Override
    public void afterInitialize(HttpServletRequest httpServletRequest, IRequest request) {

    }
}
