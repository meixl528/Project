package com.ssm.sys.controller.sys;

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

import com.ssm.core.request.IRequest;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.dto.Code;
import com.ssm.sys.dto.CodeValue;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.sys.service.ICodeService;

/**
 * 快速编码Controller.
 * @author meixl
 */
@Controller
public class CodeController extends BaseController {

    @Autowired
    private ICodeService codeService;

    /**
     * 获取快速编码对象.
     * 
     * @param code
     *            Code
     * @param page
     *            起始页
     * @param pagesize
     *            分页大小
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/code/query")
    @ResponseBody
    public ResponseData getCodes(Code code, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(codeService.selectCodes(requestContext, code, page, pagesize));
    }

    /**
     * 查询快速编码值.
     * 
     * @param value
     *            CodeValue
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/codevalue/query")
    @ResponseBody
    public ResponseData getCodeValues(CodeValue value, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(codeService.selectCodeValues(requestContext, value));
    }

    /**
     * 删除快速编码.
     * 
     * @param codes
     *            codes
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/code/remove", method = RequestMethod.POST)
    public ResponseData removeCodes(@RequestBody List<Code> codes, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        codeService.batchDelete(requestContext, codes);
        return new ResponseData();
    }

    /**
     * 删除快速编码值.
     * 
     * @param values
     *            values
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/codevalue/remove", method = RequestMethod.POST)
    public ResponseData removeValues(@RequestBody List<CodeValue> values, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        codeService.batchDeleteValues(requestContext, values);
        return new ResponseData();
    }

    /**
     * 提交快速编码对象.
     * 
     * @param codes
     *            codes
     * @param result
     *            BindingResult
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/code/submit", method = RequestMethod.POST)
    public ResponseData submitCode(@RequestBody List<Code> codes, BindingResult result, HttpServletRequest request) {
        getValidator().validate(codes, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(codeService.batchUpdate(requestContext, codes));
    }
}