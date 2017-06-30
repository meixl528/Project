package com.ssm.function.controller;

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
import com.ssm.function.dto.Resource;
import com.ssm.function.dto.ResourceItem;
import com.ssm.function.service.IResourceItemService;
import com.ssm.function.service.IResourceService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * 资源管理.
 */

@Controller
public class ResourceController extends BaseController {

    @Autowired
    private IResourceService resourceService;
    
    @Autowired
    private IResourceItemService resourceItemService;
    
    //@Autowired
    //private IResourceCustomizationService resourceCustomizationService;

    /**
     * 查询资源权限项.
     * 
     * @param request
     *            HttpServletRequest
     * @param resource
     *            Resource
     * 
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/resourceItem/query")
    @ResponseBody
    public ResponseData queryResourceItems(HttpServletRequest request, Resource resource) {
        return new ResponseData(resourceItemService.selectResourceItems(createRequestContext(request), resource));
    }
    
    /**
     * 保存资源权限项.
     * 
     * @param request
     * @param resourceItems
     * @param result
     * @return ResponseData
     * @throws BaseException
     */
    @RequestMapping(value = "/sys/resourceItem/submit")
    @ResponseBody
    public ResponseData submitResourceItem(HttpServletRequest request, @RequestBody List<ResourceItem> resourceItems,
            BindingResult result) throws BaseException {
        getValidator().validate(resourceItems, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(resourceItemService.batchUpdate(createRequestContext(request), resourceItems));
    }
    
    /**
     * 查询页面合并配置项
     * @param resourceId
     * @param page
     * @param pagesize
     * @param request
     * @return
     */
    /*@RequestMapping(value = "/sys/resourceCustomization/query")
    @ResponseBody
    public ResponseData selectResourcePathByResourceId(Long resourceId,@RequestParam(defaultValue = DEFAULT_PAGE) final int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pagesize, final HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(resourceCustomizationService.selectResourceCustomizationsByResourceId(resourceId));
    }*/
    
    /**
     * 保存页面合并配置项
     * @param request
     * @param resourcePaths
     * @param result
     * @return
     * @throws BaseException
     */
    /*@RequestMapping(value = "/sys/resourceCustomization/submit")
    @ResponseBody
    public ResponseData submitResourcePath(HttpServletRequest request, @RequestBody List<ResourceCustomization> resourceCustomizations,
            BindingResult result) throws BaseException {
        getValidator().validate(resourceCustomizations, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        return new ResponseData(resourceCustomizationService.batchUpdate(createRequestContext(request), resourceCustomizations));
    }*/
    
    /**
     * 删除页面合并配置项
     * @param request
     * @param resourcePaths
     * @return
     * @throws BaseException
     */
    /*@RequestMapping(value = "/sys/resourceCustomization/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteResourcePath(HttpServletRequest request, @RequestBody List<ResourceCustomization> resourcePaths)
            throws BaseException {
        resourceCustomizationService.batchDelete(resourcePaths);
        return new ResponseData();
    }*/
    
    /**
     * 删除资源权限项.
     * 
     * @param request
     * @param resourceItems
     * @return ResponseData
     * @throws BaseException
     */
    @RequestMapping(value = "/sys/resourceItem/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteResourceItem(HttpServletRequest request, @RequestBody List<ResourceItem> resourceItems)
            throws BaseException {
        IRequest requestContext = createRequestContext(request);
        resourceItemService.batchDelete(requestContext, resourceItems);
        return new ResponseData();
    }
    
    
    /**
     * 查询资源.
     * 
     * @param request
     *            HttpServletRequest
     * @param example
     *            Resource
     * @param page
     *            page
     * @param pagesize
     *            pagesize
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/resource/query")
    @ResponseBody
    public ResponseData getResource(HttpServletRequest request, Resource example,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(resourceService.select(requestContext, example, page, pagesize));
    }

    
    
    /**
     * 保存资源.
     * 
     * @param request
     *            HttpServletRequest
     * @param resources
     *            Resources
     * @param result
     *            BindingResult
     * @return ResponseData ResponseData
     * @throws BaseException
     *             BaseException
     */
    @RequestMapping(value = "/sys/resource/submit")
    @ResponseBody
    public ResponseData submitResource(HttpServletRequest request, @RequestBody List<Resource> resources,
            BindingResult result) throws BaseException {
        getValidator().validate(resources, result);
        if (result.hasErrors()) {
            ResponseData responseData = new ResponseData(false);
            responseData.setMessage(getErrorMessage(result, request));
            return responseData;
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(resourceService.batchUpdate(requestContext, resources));
    }

    /**
     * 删除资源.
     * 
     * @param request
     *            HttpServletRequest
     * @param resources
     *            resources
     * @return ResponseData ResponseData
     * @throws BaseException BaseException
     */
    @RequestMapping(value = "/sys/resource/remove", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteResource(HttpServletRequest request, @RequestBody List<Resource> resources)
            throws BaseException {
        IRequest requestContext = createRequestContext(request);
        resourceService.batchDelete(requestContext, resources);
        return new ResponseData();
    }

}
