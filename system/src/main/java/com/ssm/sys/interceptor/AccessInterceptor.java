package com.ssm.sys.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ssm.account.dto.Role;
import com.ssm.account.dto.User;
import com.ssm.adaptor.UrlConfig;
import com.ssm.cache.Cache;
import com.ssm.cache.CacheManager;
import com.ssm.cache.impl.RoleResourceCache;
import com.ssm.core.BaseConstants;
import com.ssm.function.dto.Resource;
/**
 * 拦截请求 ,
 * 检测session中是否有用户信息
 * @author meixl	2017年3月31日下午2:29:32
 */
public class AccessInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);
	
	/**
	 * DispatcherServlet加载的context成功后，如果 publishContext属性的值设置为true的话(缺省为true) 
	 * 会将applicationContext存放在 org.springframework.web.servlet.FrameworkServlet.CONTEXT. + (servletName)的attribute中
	 */
	private static String APP_SERVLET_CONTEXT_KEY = FrameworkServlet.SERVLET_CONTEXT_PREFIX + "springmvc";
	
	@Autowired
    private RoleResourceCache roleResourceCache;
	
	@Autowired
    private CacheManager cacheManager;

    private Cache<Resource> resourceCache;

    private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	//设置允许通过的url
	private List<String> allowedUrl = new ArrayList<>();
	public void setAllowedUrl(List<String> url) {
		if(!allowedUrl.contains(UrlConfig.VIEW_LOGIN)){
			allowedUrl.add(UrlConfig.VIEW_LOGIN);
		}
		if(url!=null && !url.isEmpty()){
			this.allowedUrl = url;
		}
	}
	
	// 在请求的方法处理之前执行
	// 如果返回为true则执行下一个拦截器 , 否不执行
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Exception {
		String url = req.getRequestURI().toString();
		String basePath = req.getContextPath();
		if(url.equals(basePath+"/"))
			return true;
		for(String u : allowedUrl){
			if(url.substring(url.lastIndexOf("/")).startsWith(u))
				return true;
		}
		//先判断session中是否有
		Object user = req.getSession().getAttribute(User.FIELD_SESSION_USER);
		if (user!=null) {
			boolean bool = csrf(req);
			return bool;
		}
		
		//webservcie测试
		if(StringUtils.isNotEmpty(req.getParameter("restApiName")) && StringUtils.isNotEmpty(req.getParameter("restApiPass"))){
			if(req.getParameter("restApiName").equals("meixl") && req.getParameter("restApiPass").equals("meixl")){
				return true;
			}else{
				System.out.println("访问用户名和密码不正确 !");
			}
		}else if(req.getAttribute("restApiName")!= null && req.getAttribute("restApiPass")!=null){
			if(req.getAttribute("restApiName").toString().equals("meixl") && req.getAttribute("restApiPass").toString().equals("meixl")){
				return true;
			}else{
				System.out.println("访问用户名和密码不正确 !");
			}
		}
		resp.sendRedirect(req.getContextPath()+UrlConfig.VIEW_LOGIN_REDIRECT);
		return false;
	}

	// 在请求的方法执行
	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object obj, ModelAndView mv)
			throws Exception {
		
		String uri = StringUtils.substringAfter(req.getRequestURI(), req.getContextPath());
		
		boolean allow = false;
		for(String u : allowedUrl){
			if(u.equals(uri) || uri.startsWith(u))
				allow = true;
		}
		if(!allow){
			HttpSession session = req.getSession();
			String uri_ori = uri;
			if (uri.startsWith("/")) {
	            uri = uri.substring(1);
	        }
			if (!"".equals(uri)) {
				Resource resource = getResourceOfUri(req, uri_ori, uri);
		        if(resource!=null){
		        	if(!pass(session,uri_ori, uri,resource) && mv!=null )
		        		mv.setViewName("403");
		        }else{
		        	if(mv!=null )
		        	   mv.setViewName("404");
		        }
	        }
		}
	}

	// 在DispatCherServlet 处理之后执行 ----清理工作
	@Override
	public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object obj, Exception e)throws Exception {
		
	}
	
	/**
	 * 跨站请求伪造(Cross Site Request Forgery)
	 * @param 官方建议引用   spring security 
	 */
	private boolean csrf(HttpServletRequest req){
		Object _csrf = req.getParameter("_csrf");
		if(_csrf!=null){
			Object csrf = req.getSession(true).getAttribute("_csrf");
			if(csrf==null || !csrf.equals(_csrf)){
				return false;
			}
		}
		return true;
	}
	
	
	private boolean pass(HttpSession session,String uri_ori,String uri,Resource resource){
    	if (!BaseConstants.YES.equalsIgnoreCase(resource.getLoginRequire())) {
            if (logger.isDebugEnabled()) {
                logger.debug("url :'{}' does not require login.", uri);
            }
            return true;
        }
        if (!BaseConstants.YES.equalsIgnoreCase(resource.getAccessCheck())) {
            if (logger.isDebugEnabled()) {
                logger.debug("url :'{}' need no access control.", uri);
            }
            return true;
        }

        if (session == null) {
        	return false;
        }
        Long roleId = (Long) session.getAttribute(Role.FIELD_ROLE_ID);
        if (roleId == null) {
        	return false;
        }
        Long[] accessible = roleResourceCache.getValue(String.valueOf(roleId));
        
        if (accessible == null || !Arrays.asList(accessible).contains(resource.getResourceId())) {
            logger.warn("access to uri :'{}' denied.", uri);
            return false;
        }
		return true;
	}
	
	private void initRequestMappingHandlerMapping(HttpServletRequest request) {
        if (requestMappingHandlerMapping != null) {
            return;
        }
        ServletContext servletContext = request.getServletContext();

        WebApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext,APP_SERVLET_CONTEXT_KEY);
        Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(appContext,
                HandlerMapping.class, true, false);
        for (HandlerMapping handlerMapping : allRequestMappings.values()) {
            if (handlerMapping instanceof RequestMappingHandlerMapping) {
                requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
                break;
            }
        }

    }
	
	private String getBestMatchPattern(HttpServletRequest request, String uri) {
        initRequestMappingHandlerMapping(request);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            List<String> patterns = requestMappingInfo.getPatternsCondition().getMatchingPatterns(uri);
            if (patterns.size() > 0) {
                return patterns.get(0);
            }
        }
        return uri;
    }
	
	@SuppressWarnings("unchecked")
	private Resource getResourceOfUri(HttpServletRequest request, String uri_ori, String uri_trim) {
        if (resourceCache == null) {
            resourceCache = (Cache<Resource>) cacheManager.getCache(BaseConstants.CACHE_RESOURCE_URL);
        }
        Resource resource = resourceCache.getValue(uri_trim);
        if (resource == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("url {} is not registered", uri_trim);
            }
            // 所有的 html 都是通过 pattern 映射的,但是这些 pattern 都没有注册
            // 每个 html 的路径都是单独注册,对于一个 html url,如果在 cache 中没找到,说明这个 url 没有注册
            // 这种情况,没必要再去解析 这个 url 对应哪个 pattern
            if (!StringUtils.endsWith(uri_ori, ".html")) {
                String pattern = getBestMatchPattern(request, uri_ori);
                if (Objects.equals(pattern, uri_ori)) {
                    // pattern and uri_ori are the same
                    return null;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("{} match pattern {}", uri_ori, pattern);
                }
                if (pattern.startsWith("/")) {
                    pattern = pattern.substring(1);
                }
                resource = resourceCache.getValue(pattern);
                if (resource == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("pattern {} is not registered", pattern);
                    }
                }
            }
        }
        return resource;
    }

}
