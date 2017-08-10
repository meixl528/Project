package com.ssm.sys.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.ognl.OgnlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.ssm.core.exception.BaseException;
import com.ssm.core.request.IRequest;
import com.ssm.core.request.impl.RequestHelper;
import com.ssm.core.util.RequestUtil;
import com.ssm.sys.responceFactory.ResponseData;

@RestController
public class BaseController {
	Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	protected static final String DEFAULT_PAGE = "1";
	protected static final String DEFAULT_PAGE_SIZE = "10";
	
	@Autowired
    protected MessageSource messageSource;
	
	@Autowired
    private Validator validator;
	
	protected Validator getValidator() {
        return validator;
    }
	
	@InitBinder
	public void initBinder(ServletRequestDataBinder dataBinder, HttpServletRequest request) {
	    dataBinder.getTarget();
	}
	
	protected IRequest createRequestContext(HttpServletRequest request) {
        return RequestHelper.createServiceRequest(request);
    }
	
	protected String getErrorMessage(Errors errors, HttpServletRequest request) {
        Locale locale = RequestContextUtils.getLocale(request);
        String errorMessage = null;
        for (ObjectError error : errors.getAllErrors()) {
            if (error.getDefaultMessage() != null) {
                errorMessage = messageSource.getMessage(error.getDefaultMessage(), null, locale);
                break;
            } else {
                errorMessage = error.getCode();
            }
        }
        return errorMessage;
    }
	
    /**
     * 处理控制层所有异常.
     *
     * @param exception
     *            未捕获的异常
     * @param request
     *            HttpServletRequest
     * @return ResponseData(BaseException 被处理) 或者 ModelAndView(其他 Exception
     *         ,500错误)
     */
    @ExceptionHandler(value = { Exception.class })
    public Object exceptionHandler(Exception exception, HttpServletRequest request) {
        logger.error(exception.getMessage(), exception);
        if (RequestUtil.isAjaxRequest(request)) {
            Throwable thr = getRootCause(exception);
            ResponseData res = new ResponseData(false);
            if (thr instanceof BaseException) {
                BaseException be = (BaseException) thr;
                Locale locale = RequestContextUtils.getLocale(request);
                String messageKey = be.getDescriptionKey();
                String message = messageSource.getMessage(messageKey, be.getParameters(), messageKey, locale);
                res.setCode(be.getCode());
                res.setMessage(message);
            } else {
                res.setMessage(thr.getMessage());
            }
            return res;
        } else {
            return new ModelAndView("500");
        }
    }
    
    private Throwable getRootCause(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        if (throwable instanceof OgnlException && ((OgnlException) throwable).getReason() != null) {
            return getRootCause(((OgnlException) throwable).getReason());
        }
        return throwable;
    }
}
