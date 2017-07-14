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
import com.ssm.mail.dto.MessageEmailWhiteList;
import com.ssm.mail.service.IMessageEmailWhiteListService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 邮件白名单Controller.
 */
@Controller
public class MessageEmailWhiteListController extends BaseController {

    @Autowired
    private IMessageEmailWhiteListService service;

    @RequestMapping(value = "/sys/messageEmailWhiteList/query")
    @ResponseBody
    public ResponseData getMessageEmailWhiteList(HttpServletRequest request, MessageEmailWhiteList example,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectMessageEmailWhiteLists(requestContext, example, page, pagesize));
    }

    @RequestMapping(value = "/sys/messageEmailWhiteList/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteMessageEmailWhiteList(HttpServletRequest request, @RequestBody List<MessageEmailWhiteList> objs) throws BaseException {
        IRequest requestContext = createRequestContext(request);
        service.batchDelete(requestContext, objs);
        return new ResponseData();
    }

}