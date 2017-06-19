/*
 * #{copyright}#
 */
package com.ssm.sys.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.adaptor.UrlUtil;
import com.ssm.cache.Cache;
import com.ssm.cache.CacheManager;
import com.ssm.cache.impl.SysCodeCache;
import com.ssm.core.BaseConstants;
import com.ssm.core.ILanguageProvider;
import com.ssm.core.impl.DefaultTableNameProvider;
import com.ssm.core.request.IRequest;
import com.ssm.core.util.DTOFieldsUtil;
import com.ssm.message.components.DefaultPromptListener;
import com.ssm.sys.dto.Code;
import com.ssm.sys.dto.CodeValue;
import com.ssm.sys.dto.Language;
import com.ssm.sys.dto.Prompt;
import com.ssm.sys.mapper.LanguageMapper;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.sys.service.ILovService;

/**
 * 通用的 Controller,用来获取公共数据.
 */
@RestController
public class CommonController extends BaseController{

    private Logger logger = LoggerFactory.getLogger(CommonController.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private ILovService commonLovService;
    
    @Autowired
    private ILanguageProvider languageProvider;
    
    @Autowired
    private LanguageMapper languageMapper;
    
    @Autowired
    private DefaultPromptListener promptListener;
    
    private String getCommonPrompts(String lang) {
        List<Prompt> list = promptListener.getDefaultPrompt(lang);
        if (list == null) {
            return "//null";
        }
        StringBuilder sb = new StringBuilder();
        for (Prompt prompt : list) {
            // sb.append("var
            // $L.").append(prompt.getPromptCode().toLowerCase()).append("='")
            // .append(prompt.getDescription()).append("';\n");
            sb.append("$l('").append(prompt.getPromptCode().toLowerCase()).append("','").append(prompt.getDescription())
                    .append("');\n");
        }
        sb.append("//").append(list.size());
        return sb.toString();
    }
    
    private List<CodeValue> getEnabledCodeValues(Code code){
        List<CodeValue> enabledCodeValues = new ArrayList<>();
        List<CodeValue> allCodeValues =  code.getCodeValues();
        if(allCodeValues!=null){
            for(CodeValue codevalue:allCodeValues){
                if(BaseConstants.YES.equals(codevalue.getEnabledFlag())){
                    enabledCodeValues.add(codevalue);
                }
            }
        }
        return  enabledCodeValues;
    }
    
    /**
     * 获取通用数据.
     * 
     * @param resource
     *            资源类型
     * @param params
     *            参数
     * @param request
     *            HttpServletRequest
     * @return json值
     * @throws JsonProcessingException
     *             对象转 JSON 可能出现的异常
     */
    @RequestMapping(value = "/common/{resource}", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getCommonData(@PathVariable String resource, @RequestParam Map<String, String> params,
            HttpServletRequest request) throws JsonProcessingException {
    	Locale locale = RequestContextUtils.getLocale(request);
        String lang = locale == null ? null : locale.toString();

        if ("prompts".equalsIgnoreCase(resource)) {
            return getCommonPrompts(lang);
        }

        Cache<?> cache = cacheManager.getCache(resource);
        String var = params.get("var");
        StringBuilder sb = new StringBuilder();
        if (cache instanceof SysCodeCache) {
            params.forEach((k, v) -> {
                Code code = ((SysCodeCache) cache).getValue(v + "." + lang);
                try {
                    if (code == null) {
                        toJson(sb, k, Collections.EMPTY_LIST);
                    } else {
                        if(BaseConstants.NO.equals(code.getEnabledFlag())){
                            toJson(sb, k, Collections.EMPTY_LIST);
                        }else {
                            List<CodeValue> enabledCodeValues =getEnabledCodeValues(code);
                            if(enabledCodeValues==null){
                                toJson(sb, k, Collections.EMPTY_LIST);
                            }else{
                                toJson(sb, k, enabledCodeValues);
                            }
                        }
                    }
                    sb.append("\n");
                } catch (JsonProcessingException e) {
                    if (logger.isErrorEnabled()) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });
        } else if ("language".equals(resource)) {
            List<?> data = languageProvider.getSupportedLanguages();
            toJson(sb, var, data);
        }
        return sb.toString();
    }

    /**
     * 取 快码 专用(仅限一个).
     * <p>
     * 仅返回code 的内容(作为数组),没有额外的内容.
     * 
     * @param code
     *            快码 code
     * @param request
     *            request
     * @return json array
     * @throws JsonProcessingException
     *             对象转 JSON 可能出现的异常
     */
    @RequestMapping(value = "/common/code/{code}/", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getCommonCode(@PathVariable String code, HttpServletRequest request) throws JsonProcessingException {
        Locale locale = RequestContextUtils.getLocale(request);

        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    /**
     * 通用LOV的查询url.
     * 
     * @param id
     *            lovId
     * @param page
     *            起始页
     * @param pagesize
     *            分页大小
     * @param params
     *            参数
     * @param request
     *            HttpServletRequest
     * @return ResponseData ResponseData
     */
    @RequestMapping(value = "/common/lov/{id}")
    @ResponseBody
    public ResponseData getLovDatas(@PathVariable String id, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, @RequestParam Map<String, String> params,
            HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
        return new ResponseData(commonLovService.selectDatas(requestContext, id, params, page, pagesize));
    }

    @RequestMapping(value = "/{folder1}/{name}.html")
    public ModelAndView renderFolder1View(@PathVariable String folder1, @PathVariable String name, Model model) {
        return new ModelAndView(new StringBuilder("/").append(folder1).append("/").append(name).append(UrlUtil.URL_SUFFIX).toString());
    }

    @RequestMapping(value = "/{folder1}/{folder2}/{name}.html")
    public ModelAndView renderFolder2View(@PathVariable String folder1, @PathVariable String folder2,
            @PathVariable String name, Model model) {
        return new ModelAndView(new StringBuilder("/").append(folder1).append("/")
        		.append(folder2).append("/").append(name).append(UrlUtil.URL_SUFFIX).toString());
    }

    @RequestMapping(value = "/{folder1}/{folder2}/{folder3}/{name}.html")
    public ModelAndView renderFolder3View(@PathVariable String folder1, @PathVariable String folder2,
            @PathVariable String folder3, @PathVariable String name, Model model) {
        return new ModelAndView(new StringBuilder("/").append(folder1).append("/")
        		.append(folder2).append("/").append(folder3).append("/")
        		.append(name).append(UrlUtil.URL_SUFFIX).toString());
    }

    @RequestMapping(value = "/{folder1}/{folder2}/{folder3}/{folder4}/{name}.html")
    public ModelAndView renderFolder4View(@PathVariable String folder1, @PathVariable String folder2,
            @PathVariable String folder3, @PathVariable String folder4, @PathVariable String name, Model model) {
        return new ModelAndView();
    }

    @RequestMapping(value = "/{name}.html")
    public ModelAndView renderView(@PathVariable String name, Model model) {
        return new ModelAndView(name + UrlUtil.URL_SUFFIX);
    }

    protected void toJson(StringBuilder sb, String var, Object data) throws JsonProcessingException {
        boolean hasVar = var != null && var.length() > 0;
        if (hasVar) {
            sb.append("var ").append(var).append('=');
        }
        sb.append(objectMapper.writeValueAsString(data));
        if (hasVar) {
            sb.append(';');
        }
    }

    /**
     * 处理多语言字段.
     *
     * @param request
     *            HttpServletRequest
     * @param id
     *            主键值
     * @param dto
     *            dto全名
     * @param field
     *            多语言字段名称(dto中的属性名)
     * @return 视图
     */
    @RequestMapping(value = "sys/sys_multilanguage_editor.html")
    public ModelAndView loadMultiLanguageFields(HttpServletRequest request, @RequestParam String id,
            @RequestParam String dto, @RequestParam String field) {
        ModelAndView view = new ModelAndView();
        if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(dto) && StringUtils.isNotEmpty(field)) {
            Class<?> clazz;
        }
        return view;
    }
    
    /**
     * 处理多语言字段.
     * 
     * @param <T>
     *
     * @param request
     *            HttpServletRequest
     * @param id
     *            主键值
     * @param dto
     *            dto全名
     * @param field
     *            多语言字段名称(dto中的属性名)
     * @return Map
     */
    @RequestMapping(value = "/sys/multiLanguage", method = RequestMethod.GET)
    public Map<String, Object> loadMultiLanguageFields2(HttpServletRequest request, @RequestParam String id,
            @RequestParam String dto, @RequestParam String field) {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(dto) && StringUtils.isNotEmpty(field)) {
            Class<?> clazz;
            try {
                clazz = Class.forName(dto);
                Table table = clazz.getAnnotation(Table.class);
                Field idField = DTOFieldsUtil.getFieldsWithAnnotation(clazz, Id.class)[0];
                if (table != null && idField != null) {
                    Map<String, String> map = new HashMap<>();
                    map.put("table", DefaultTableNameProvider.getInstance().getTlTableName(table.name()));
                    map.put("idName", DTOFieldsUtil.getColumnName(idField));
                    map.put("tlName", DTOFieldsUtil.getColumnName(clazz,field));
                    map.put("id", id);
                    result.put("multiLanguages", languageMapper.selectFor(map));
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        } else {
            List<Language> list = languageProvider.getSupportedLanguages();
            list.sort((a, b) -> a.getLangCode().compareTo(b.getLangCode()));
            result.put("multiLanguages", list);
        }
        return result;
    }
    

    /**
     * 获取配置文件.
     * 
     * @param params
     *            参数
     * @param request
     *            HttpServletRequest
     * @return json值
     */
    @RequestMapping(value = "/common/profile", produces = "application/javascript;charset=utf8")
    @ResponseBody
    public String getProfile(@RequestParam Map<String, String> params, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
