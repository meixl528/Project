package com.ssm.function.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.core.request.IRequest;
import com.ssm.function.dto.Function;
import com.ssm.function.dto.Resource;
import com.ssm.function.service.IFunctionService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * ThreadLocal<?> 线程局部变量
 * <p>为每个执行该方法的线程创建一个IRequest对象.</p>
 * <p>IRequest对象 中可存储相关类容,各ThreadLocal<?>之间互不影响</p>
 * @see com.ssm.core.request.impl.RequestHelper
 */
@Controller
public class FunctionController extends BaseController{
	
	@Autowired
	private IFunctionService functionService;
	
	/**
     * 主界面菜单数据获取.
     */
    @RequestMapping("sys/function/menus")
    @ResponseBody
    public Object getMenuTree(HttpServletRequest request) {
		return functionService.selectRoleFunctions(createRequestContext(request));
    }
    
    /**
     * 获取挂靠功能资源.
     * @param functionId
     *            功能ID
     * @param isExit
     *            isExit
     */
    @RequestMapping(value = "sys/function/fetchResource")
    @ResponseBody
    public ResponseData fetch(final HttpServletRequest request,@ModelAttribute Function function, final Integer isExit,
            final Resource resource, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
    	/**
    	 * ThreadLocal<?> 线程局部变量
    	 * 为每个执行该方法的线程创建一个IRequest对象.
    	 * IRequest对象 中可存储相关类容,各ThreadLocal<?>之间互不影响
    	 */
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(functionService.selectExitResourcesByFunction(requestContext, function, resource, page, pagesize));
    }
    
    /**
     * 获取未挂靠的功能资源.
     *
     * @param request
     *            HttpServletRequest
     * @param functionId
     *            功能ID
     * @param isExit
     *            isExit
     * @param resource
     *            资源
     * @param page
     *            起始页
     * @param pagesize
     *            分页大小
     * @return ResponseData
     */
    @RequestMapping(value = "sys/function/fetchNotResource")
    @ResponseBody
    public ResponseData fetch2(final HttpServletRequest request, final Long functionId, final Integer isExit,
                               final Resource resource, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                               @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize) {
        IRequest requestContext = createRequestContext(request);
        Function function = new Function();
        if (StringUtils.isEmpty(resource.getUrl())) {
            resource.setUrl(null);
        }
        if (StringUtils.isEmpty(resource.getName())) {
            resource.setName(null);
        }
        if (StringUtils.isEmpty(resource.getType())) {
            resource.setType(null);
        }
        function.setFunctionId(functionId);
        return new ResponseData(
                functionService.selectNotExitResourcesByFunction(requestContext, function, resource, page, pagesize));
    }
    
    /**
     * 查询功能
     */
    @RequestMapping(value = "/sys/function/query")
    @ResponseBody
    public ResponseData query(final Function function, @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
        return new ResponseData(functionService.selectFunction(requestContext, function, page, pagesize));
    }
    
    /**
     * 插入和更新功能
     * @param functions
     * @param request
     * @return
     */
    @RequestMapping(value = "/sys/function/submit")
    @ResponseBody
    public ResponseData submit(@RequestBody final List<Function> functions,final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(functionService.batchUpdate(requestContext, functions));
    }
    
    /**
     * 删除功能.
     * @param functions
     */
    @RequestMapping(value = "/sys/function/remove")
    @ResponseBody
    public ResponseData remove(@RequestBody final List<Function> functions, final BindingResult result,
            final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        functionService.batchDelete(requestContext, functions);
        return new ResponseData();
    }
    
    /**
     * 更新功能资源.
     *
     * @param request
     *            HttpServletRequest
     * @param function
     *            功能
     * @return ResponseData ResponseData
     */
    @RequestMapping(value = "/sys/function/updateFunctionResource")
    @ResponseBody
    public ResponseData updateFunctionResource(final HttpServletRequest request, @RequestBody final Function function) {
        IRequest requestContext = createRequestContext(request);
        functionService.updateFunctionResources(requestContext, function, function.getResources());
        return new ResponseData();
    }
}
