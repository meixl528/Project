package com.ssm.mail.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.core.exception.BaseException;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageAccount;
import com.ssm.mail.service.IMessageAccountService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 消息账号Controller.
 */
@Controller
public class MessageAccountController extends BaseController {

    @Autowired
    private IMessageAccountService service;

    @RequestMapping(value = "/sys/messageAccount/query")
    @ResponseBody
    public ResponseData getMessageAccount(HttpServletRequest request, MessageAccount example,
                                          @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageAccounts(requestContext, example, page, pagesize));
    }

    @RequestMapping(value = "/sys/messageAccount/queryAccount")
    @ResponseBody
    public ResponseData getMessageAccountPassword(HttpServletRequest request, MessageAccount example) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageAccountPassword(requestContext, example, 1, 1));
    }
    
    @ResponseBody
    @RequestMapping(value="/sys/messageAccount/add")
    public ResponseData addMessageAccount(HttpServletRequest request, @RequestBody MessageAccount obj, BindingResult result) throws BaseException {
        //没意义的值
        obj.setObjectVersionNumber(0L);
        
        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.createMessageAccount(requestContext, obj);
        return new ResponseData();
    }
    
    @ResponseBody
    @RequestMapping(value="/sys/messageAccount/update")
    public ResponseData updateMessageAccount(HttpServletRequest request, @RequestBody MessageAccount obj, BindingResult result) throws BaseException {
        //没意义的值
        obj.setObjectVersionNumber(0L);
        
        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.updateMessageAccount(requestContext, obj);
        return new ResponseData();
    }
    
    @ResponseBody
    @RequestMapping(value="/sys/messageAccount/updatePasswordOnly")
    public ResponseData updateMessageAccountPasswordOnly(HttpServletRequest request, @RequestBody MessageAccount obj, BindingResult result) throws BaseException {
        //没意义的值
        obj.setObjectVersionNumber(0L);
        
        getValidator().validate(obj, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        service.updateMessageAccountPasswordOnly(requestContext, obj);
        return new ResponseData();
    }
    
    @RequestMapping(value = "/sys/messageAccount/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteMessageAccount(HttpServletRequest request, @RequestBody List<MessageAccount> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}