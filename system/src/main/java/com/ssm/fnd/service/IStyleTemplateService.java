package com.ssm.fnd.service;

import com.ssm.core.proxy.ProxySelf;
import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.sys.service.IBaseService;

/**
 * @name        IStyleTemplateService
 * @description 样式模版管理服务接口类
 * @author      meixl
 */
public interface IStyleTemplateService extends IBaseService<StyleTemplate>, ProxySelf<IStyleTemplateService>{
	
	final String STYLE_TEMPLATE = "style_template";
	
	/**
     * 获取样式模板对象
     * @param templateCode 模版代码
     * @return
     */
	public StyleTemplate getStyleTemplate(String templateCode);
	
    /**
     * 获取样式模板内容
     * @param templateCode 模版代码
     * @return
     */
	public String getStyleTemplateStr(String templateCode);

	/**
	 * 从数据库查询  模板
	 * @param templateCode
	 * @return
	 */
	public StyleTemplate getStyleTemplateFromDB(String templateCode);
	
}