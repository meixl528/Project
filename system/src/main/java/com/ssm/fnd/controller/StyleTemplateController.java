package com.ssm.fnd.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.core.request.IRequest;
import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.fnd.service.IStyleTemplateService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * @name        StyleTemplateController
 * @description 样式模板管理控制器
 * @author      meixl
 */
@Controller
public class StyleTemplateController extends BaseController {

	@Autowired
	private IStyleTemplateService service;

	@RequestMapping(value = "/fnd/styleTemplate/query")
	@ResponseBody
	public ResponseData query(StyleTemplate dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.select(requestContext, dto, page, pageSize));
	}

	@RequestMapping(value = "/fnd/styleTemplate/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<StyleTemplate> dto) {
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(service.batchUpdate(requestCtx, dto));
	}

	@RequestMapping(value = "/fnd/styleTemplate/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<StyleTemplate> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}
	
    /**
     * 删除内容行信息
     * @param request
     * @param page
     * @return
     */
    @RequestMapping(value = "/fnd/styleTemplate/removeRow", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData deleteRowGuide(HttpServletRequest request,StyleTemplate template) {
    	int i = service.deleteByPrimaryKey(template);
    	if(i > 0){
    		return new ResponseData(true);
    	}
        //获取当前异常
        ResponseData responseData = new ResponseData(false);
        responseData.setMessage("Data Delete Error");
        return responseData;
    }
}