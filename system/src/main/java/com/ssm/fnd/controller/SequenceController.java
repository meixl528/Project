package com.ssm.fnd.controller;

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
import com.ssm.fnd.dto.Sequence;
import com.ssm.fnd.service.ISequenceService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

/**
 * @name SequenceController
 * @description 序列号规则配置
 * @author meixl
 */
@Controller
public class SequenceController extends BaseController {

	@Autowired
	private ISequenceService service;

	@RequestMapping(value = "/fnd/sequence/query")
	public ResponseData query(Sequence dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.select(requestContext, dto, page, pageSize));
	}

	@RequestMapping(value = "/fnd/sequence/submit", method = RequestMethod.POST)
	public ResponseData update(@RequestBody List<Sequence> sequences, BindingResult result, HttpServletRequest request) {
		getValidator().validate(sequences, result);
		if (result.hasErrors()) {
			ResponseData rd = new ResponseData(false);
			rd.setMessage(getErrorMessage(result, request));
			return rd;
		}
		IRequest requestCtx = createRequestContext(request);
		return new ResponseData(service.batchUpdate(requestCtx, sequences));
	}

	@RequestMapping(value = "/fnd/sequence/remove", method = RequestMethod.POST)
	public ResponseData delete(HttpServletRequest request, @RequestBody List<Sequence> sequences) {
		service.batchDelete(sequences);
		return new ResponseData();
	}
	
	@RequestMapping(value = "/fnd/sequence/getSequence")
	@ResponseBody
	public String getSequence(HttpServletRequest request, @RequestParam String sequenceCode) {
		return service.getSequence(sequenceCode);
	}
	
}