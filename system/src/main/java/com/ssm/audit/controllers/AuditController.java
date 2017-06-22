/*
 * #{copyright}#
 */

package com.ssm.audit.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.audit.dto.Audit;
import com.ssm.audit.service.IAuditService;
import com.ssm.core.exception.BaseException;
import com.ssm.core.request.IRequest;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 审计开关查询及保存.
 */
@Controller
public class AuditController extends BaseController {

    @Autowired
    private IAuditService auditService;

    /**
     * 审计开关查询.
     *
     * @param request
     *            HttpServletRequest
     * @return ResponseData ResponseData
     */
    @RequestMapping(value = "/sys/audit/queryAll")
    @ResponseBody
    public ResponseData getUserRoleIds(HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(auditService.selectAuditEntityAll(requestContext));
    }

    /**
     * 保存审计开关.
     *
     * @param request
     *            HttpServletRequest
     * @param audits
     *            审计开关列表
     * @param result
     *            BindingResult
     * @return ResponseData ResponseData
     * @throws BaseException
     *             BaseException
     */
    @RequestMapping(value = "/sys/audit/save", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData submitUserRole(HttpServletRequest request, @RequestBody List<Audit> audits,
                                       BindingResult result) throws BaseException {
        getValidator().validate(audits, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(auditService.saveAuditEntityAll(requestContext, audits));
    }

}
