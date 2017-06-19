package com.ssm.core.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.template.SimpleHash;
/**
 * @name        DefaultFreeMarkerView
 * @description 可以添加service到model,这样页面可以使用${XXXservice.get()}来调用后台方法
 * @author      meixl
 * @date        2017年5月9日下午3:11:16
 * @version
 */
public class DefaultFreeMarkerView extends FreeMarkerView {

    /*private ILovService lovService;
    private IAccessService accessService;*/
    private FreeMarkerBeanProvider beanProvider;

    protected FreeMarkerConfig autodetectConfiguration() throws BeansException {
        /*lovService = getApplicationContext().getBean(ILovService.class);
        accessService = getApplicationContext().getBean(IAccessService.class);*/
        beanProvider = getApplicationContext().getBean(FreeMarkerBeanProvider.class);
        return super.autodetectConfiguration();
    }

    protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request,
            HttpServletResponse response) {
        SimpleHash fmModel = super.buildTemplateModel(model, request, response);
        /*accessService.setRequest(request);
        fmModel.put("lovService", lovService);
        fmModel.put("accessService", accessService);*/
        Map<String, Object> beans = beanProvider.getAvailableBean();
        if (beans != null) {
            fmModel.putAll(beans);
        }
        return fmModel;
    }
}