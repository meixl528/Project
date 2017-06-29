package com.ssm.fnd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.cache.Cache;
import com.ssm.cache.CacheManager;
import com.ssm.cache.annotation.CacheDelete;
import com.ssm.cache.annotation.CacheSet;
import com.ssm.cache.impl.HashStringRedisCache;
import com.ssm.core.request.IRequest;
import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.fnd.service.IStyleTemplateService;
import com.ssm.sys.service.impl.BaseServiceImpl;
/**
 * @name        StyleTemplateServiceImpl
 * @description 样式模版管理服务实现类
 * @author      meixl
 */
@Service
@Transactional
public class StyleTemplateServiceImpl extends BaseServiceImpl<StyleTemplate> implements IStyleTemplateService{
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Override
    @CacheSet(cache = STYLE_TEMPLATE)
    public StyleTemplate insertSelective(IRequest request, StyleTemplate template) {
        super.insertSelective(request, template);
        return template;
    }

    @Override
    @CacheDelete(cache = STYLE_TEMPLATE)
    public int deleteByPrimaryKey(StyleTemplate fileObject) {
        return super.deleteByPrimaryKey(fileObject);
    }

    @Override
    @CacheSet(cache = STYLE_TEMPLATE)
    public StyleTemplate updateByPrimaryKeySelective(IRequest request, StyleTemplate template) {
        super.updateByPrimaryKeySelective(request, template);
        return template;
    }
    
    /**
     * 获取样式模板对象
     * @param templateCode 模版代码
     * @return
     */
    @SuppressWarnings("unchecked")
	public StyleTemplate getStyleTemplate(String templateCode){
		// 获取打印模板
		Cache<?> cache = applicationContext.getBean(CacheManager.class).getCache(STYLE_TEMPLATE);
		StyleTemplate styleTemplate = ((HashStringRedisCache<StyleTemplate>)cache).getValue(templateCode);
        return styleTemplate;
    }
    
    /**
     * 获取样式模板内容
     * @param templateCode 模版代码
     * @return
     */
    @SuppressWarnings("unchecked")
	public String getStyleTemplateStr(String templateCode){
    	String templateContent = null;
		// 获取打印模板
		Cache<?> cache = applicationContext.getBean(CacheManager.class).getCache(STYLE_TEMPLATE);
		StyleTemplate styleTemplate = ((HashStringRedisCache<StyleTemplate>)cache).getValue(templateCode);
		if(styleTemplate != null){
			templateContent = styleTemplate.getTemplateContent();
		}
        return templateContent;
    }
	
}