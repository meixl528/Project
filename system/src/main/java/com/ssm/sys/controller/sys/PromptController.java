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

import com.ssm.core.exception.TokenException;
import com.ssm.core.request.IRequest;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.dto.Prompt;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.sys.service.IPromptService;

/**
 * PromptController.
 * @author meixl
 */
@Controller
public class PromptController extends BaseController {

    @Autowired
    private IPromptService promptService;

    /**
     * 描述信息查询.
     * 
     * @param prompt
     *            Prompt
     * @param page
     *            起始页
     * @param pagesize
     *            分页大小
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/prompt/query")
    @ResponseBody
    public ResponseData getPrompts(HttpServletRequest request,Prompt prompt, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(promptService.select(iRequest, prompt, page, pagesize));
    }

    /**
     * 描述信息保存.
     * 
     * @param prompts
     *            prompts
     * @param result
     *            BindingResult
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     * @throws TokenException
     */
    @RequestMapping(value = "/sys/prompt/submit", method = RequestMethod.POST)
    public ResponseData submitPrompt(@RequestBody List<Prompt> prompts, BindingResult result,
            HttpServletRequest request) throws TokenException {
        //checkToken(request, prompts);
        getValidator().validate(prompts, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(promptService.batchUpdate(requestCtx, prompts));
    }

    /**
     * 描述信息删除.
     * 
     * @param request
     * @param prompts
     * @return ResponseData
     * @throws TokenException
     */
    @RequestMapping(value = "/sys/prompt/remove", method = RequestMethod.POST)
    public ResponseData remove(HttpServletRequest request, @RequestBody List<Prompt> prompts) throws TokenException {
        //checkToken(request, prompts);
        promptService.batchDelete(prompts);
        return new ResponseData();
    }
}