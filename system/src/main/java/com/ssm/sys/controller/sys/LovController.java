package com.ssm.sys.controller.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.ssm.sys.dto.Lov;
import com.ssm.sys.dto.LovItem;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.sys.service.IKendoLovService;
import com.ssm.sys.service.ILovService;

/**
 * 通用lov的Controller.
 * @author meixl
 */
@Controller
public class LovController extends BaseController {

    @Autowired
    private ILovService lovService;
    
    @Autowired
    private IKendoLovService kendoLovService;
    
    /**
     * 通用lov配置项查询.
     * 
     * @param item LovItem
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/lovitem/query")
    @ResponseBody
    public ResponseData getLovItems(LovItem item, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(lovService.selectLovItems(requestContext, item));
    }
    
    /**
     * 通用lov查询.
     * 
     * @param lov Lov
     * @param page 起始页
     * @param pagesize 分页大小
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/lov/query")
    @ResponseBody
    public ResponseData queryLov(Lov lov, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(lovService.selectLovs(requestContext, lov, page, pagesize));
    }
    
    /**
     * 根据动态的lovCode获取LOV配置
     * @param contextPath
     * @param locale
     * @param lovCode
     * @return
     */
    @RequestMapping(value = "/sys/lov/getLovByCustomCode")
    @ResponseBody
    public String getLovByCustomCode(@RequestParam("contextPath")String contextPath, @RequestParam("locale")Locale locale, @RequestParam("lovCode")String lovCode){
        return kendoLovService.getLov(contextPath, locale, lovCode);
    }
    
    
    /**
     * 加载通用lov.
     * 
     * @param lovId id
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/lov/load")
    @ResponseBody
    public ResponseData loadLov(@RequestParam Long lovId, HttpServletRequest request) {
        List<Lov> list = new ArrayList<>();
        list.add(lovService.loadLov(lovId));
        return new ResponseData(list);
    }
    
    /**
     * 删除通用lov.
     * 
     * @param items items
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/lov/remove", method = RequestMethod.POST)
    public ResponseData removeLov(@RequestBody List<Lov> items) {
        lovService.batchDeleteLov(items);
        return new ResponseData();
    }
    
    /**
     * 删除通用lov配置项.
     * 
     * @param items LovItem
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/lovitem/remove", method = RequestMethod.POST)
    public ResponseData removeLovItems(@RequestBody List<LovItem> items) {
        lovService.batchDeleteItems(items);
        return new ResponseData();
    }
    
    /**
     * 保存通用lov. 
     * @param lovs lovs
     * @param result BindingResult
     * @param request HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/lov/submit", method = RequestMethod.POST)
    public ResponseData submitLov(@RequestBody List<Lov> lovs, BindingResult result, HttpServletRequest request) {
        // validator.validate(codes, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(lovService.batchUpdate(requestContext, lovs));
    }
}