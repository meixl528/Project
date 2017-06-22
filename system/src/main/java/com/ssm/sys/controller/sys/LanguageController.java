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
import com.ssm.sys.dto.Language;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.sys.service.ILanguageService;

/**
 * language
 * @author meixl
 */
@Controller
@RequestMapping()
public class LanguageController extends BaseController {

    @Autowired
    private ILanguageService languageService;

    @RequestMapping(value = "sys/language/query", method = RequestMethod.POST)
    public @ResponseBody
    ResponseData query(HttpServletRequest request, Language example,
                       @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(languageService.select(iRequest, example, page, pagesize));
    }

    @RequestMapping(value = "sys/language/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData submit(HttpServletRequest request, @RequestBody List<Language> languageList,
            BindingResult result) {
        getValidator().validate(languageList, result);
        if (result.hasErrors()) {
            ResponseData rs = new ResponseData(false);
            rs.setMessage(getErrorMessage(result, request));
            return rs;
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(languageService.batchUpdate(iRequest, languageList));
    }

    @RequestMapping(value = "sys/language/delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<Language> languageList) {
        languageService.batchDelete(languageList);
        return new ResponseData();
    }
}

