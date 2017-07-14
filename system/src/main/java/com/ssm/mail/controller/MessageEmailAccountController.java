package com.ssm.mail.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.core.exception.BaseException;
import com.ssm.core.request.IRequest;
import com.ssm.mail.dto.MessageEmailAccount;
import com.ssm.mail.service.IMessageEmailAccountService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 邮件账号Controller.
 */
@Controller
public class MessageEmailAccountController extends BaseController {

    @Autowired
    private IMessageEmailAccountService service;

    @RequestMapping(value = "/sys/messageEmailAccount/query")
    @ResponseBody
    public ResponseData getMessageEmailAccount(HttpServletRequest request, MessageEmailAccount example,
                                               @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageEmailAccounts(requestContext, example, page, pagesize));
    }

    @RequestMapping(value = "/sys/messageEmailAccount/queryAccount")
    @ResponseBody
    public ResponseData getMessageEmailAccountPassword(HttpServletRequest request, MessageEmailAccount example) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageEmailAccountWithPassword(requestContext, example, 1, 1));
    }
    
    @RequestMapping(value = "/sys/messageEmailAccount/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteMessageEmailAccount(HttpServletRequest request, @RequestBody List<MessageEmailAccount> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}