/*
 * #{copyright}#
 */
package com.ssm.adaptor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.account.dto.Role;
import com.ssm.account.dto.RoleExt;
import com.ssm.account.dto.User;
import com.ssm.account.exception.RoleException;
import com.ssm.account.exception.UserException;
import com.ssm.account.service.IRoleService;
import com.ssm.account.service.IUserService;
import com.ssm.account.service.impl.AccountConfig;
import com.ssm.adaptor.ILoginAdaptor;
import com.ssm.adaptor.ISaveIpAddressListener;
import com.ssm.adaptor.UrlConfig;
import com.ssm.adaptor.dto.CSRF;
import com.ssm.captcha.service.ICaptchaManager;
import com.ssm.captcha.service.impl.CaptchaConfig;
import com.ssm.core.BaseConstants;
import com.ssm.core.ILanguageProvider;
import com.ssm.core.request.IRequest;
import com.ssm.core.request.impl.RequestHelper;
import com.ssm.core.util.TimeZoneUtil;
import com.ssm.sys.dto.Language;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.util.CookieUtils;
import com.ssm.util.StringUtil;

/**
 * 默认登陆代理类.
 * @author URL和页面分开
 */
@Component
public class DefaultLoginAdaptor implements ILoginAdaptor{
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	@Qualifier(value="objectMapper")
	private ObjectMapper objectMapper;

    @Autowired
    private ICaptchaManager captchaManager;

    @Autowired
    private ILanguageProvider languageProvider;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserService userService;
    
    @Autowired
    private CaptchaConfig captchaConfig;

    public ModelAndView doLogin(User user, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView();
        Locale locale = RequestContextUtils.getLocale(request);

        view.setViewName(UrlConfig.VIEW_LOGIN);
        try {
            beforeLogin(view, user, request, response);
            checkCaptcha(request, response);
            user = userService.login(user);
            HttpSession session = request.getSession(true);
            session.setAttribute(User.FIELD_USER_ID, user.getUserId());
            session.setAttribute(User.FIELD_USER_NAME, user.getUserName());
            session.setAttribute(IRequest.FIELD_LOCALE, locale.toString());
            setTimeZoneFromPreference(session, user.getUserId());
//            generateSecurityKey(session);
            afterLogin(view, user, request, response);
        } catch (UserException e) {
            view.addObject("msg", messageSource.getMessage(e.getCode(), e.getParameters(), locale));
            view.addObject("code", e.getCode());
            processLoginException(view, user, e, request, response);
        }
        return view;
    }

    private void setTimeZoneFromPreference(HttpSession session, Long accountId) {
        String tz = "GMT+0800";
        if (StringUtils.isBlank(tz)) {
            tz = TimeZoneUtil.toGMTFormat(TimeZone.getDefault());
        }
        session.setAttribute(BaseConstants.TIME_ZONE, tz);
    }

//    private String generateSecurityKey(HttpSession session) {
//        return TokenUtils.setSecurityKey(session);
//    }

    /**
     * 登陆前逻辑.
     *
     * @param view
     *            视图
     * @param account
     *            账号
     * @param request
     *            请求
     * @param response
     *            响应
     * @throws UserException
     *             异常
     */
    protected void beforeLogin(ModelAndView view, User account, HttpServletRequest request,
            HttpServletResponse response) throws UserException {

    }

    /**
     * 处理登陆异常.
     *
     * @param view
     * @param account
     * @param e
     * @param request
     * @param response
     */
    protected void processLoginException(ModelAndView view, User account, UserException e, HttpServletRequest request,
            HttpServletResponse response) {

    }

    /**
     * 校验验证码是否正确.
     * @param request 请求
     * @param response 响应
     * @throws UserException  异常
     */
    private void checkCaptcha(HttpServletRequest request, HttpServletResponse response)
            throws UserException {
        if (captchaConfig.isEnableCaptcha(WebUtils.getCookie(request, CaptchaConfig.LOGIN_KEY))) {
            Cookie cookie = WebUtils.getCookie(request, captchaManager.getCaptchaKeyName());
            String captchaCode = request.getParameter(UrlConfig.KEY_VERIFICODE);
            if (cookie == null || StringUtils.isEmpty(captchaCode) || !captchaManager.checkCaptcha(cookie.getValue(), captchaCode)) {
                throw new UserException(UserException.ERROR_INVALID_CAPTCHA, UserException.ERROR_INVALID_CAPTCHA,null);
            }
        }
    }

    /**
     * 账号登陆成功后处理逻辑.
     *
     * @param view
     *            视图
     * @param user
     *            账号
     * @param request
     *            请求
     * @param response
     *            响应
     * @throws UserException
     *             异常
     */
    protected void afterLogin(ModelAndView view, User user, HttpServletRequest request, HttpServletResponse response)
            throws UserException {
        view.setViewName(UrlConfig.REDIRECT + UrlConfig.VIEW_ROLE_SELECT_REDIRECT);
        Cookie cookie = new Cookie(User.FIELD_USER_NAME, user.getUserName());
        cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    @Override
    public ModelAndView doSelectRole(Long roleId, HttpServletRequest request, HttpServletResponse response) throws RoleException {
        ModelAndView result = new ModelAndView();
        // 选择角色
        HttpSession session = request.getSession(false);
        if (session != null) {
        	if(roleId ==null){
            	roleId = (Long) session.getAttribute(Role.FIELD_ROLE_ID);
            }
        	if(roleId!=null){
        		Long userId = (Long) session.getAttribute(User.FIELD_USER_ID);
        		Role role =  roleService.checkUserRoleExists(request.getLocale().toString(),userId, roleId);
                session.setAttribute(Role.FIELD_ROLE_ID, role.getRoleId());
                session.setAttribute(Role.FIELD_ROLE_NAME, role.getRoleName());
                result.setViewName(UrlConfig.REDIRECT + UrlConfig.VIEW_WELCOME_REDIRECT);
        	}
        } else {
            result.setViewName(UrlConfig.REDIRECT + UrlConfig.VIEW_LOGIN_REDIRECT);
        }
        return result;
    }

    /**
     * 填充系统语言数据.
     * 
     * @param view
     */
/*    protected void fillContextLanguagesData(ModelAndView view) {
        List<Language> languages = languageProvider.getSupportedLanguages();
        view.addObject("languages", languages);
    }*/

    /**
     * 集成类中可扩展此方法实现不同的userService.
     * 
     * @return IUserService
     */
    public IUserService getUserService() {
        return userService;
    }

    /**
     * @param request
     * @param response
     * @return
     */
    @Override
    public ModelAndView indexView(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
        	Long sessionUserId = (Long) session.getAttribute(User.FIELD_USER_ID);
        	if (sessionUserId == null) {
                return new ModelAndView(UrlConfig.REDIRECT + UrlConfig.VIEW_LOGIN_REDIRECT);
            }
            Long sessionRoleId = (Long) session.getAttribute(Role.FIELD_ROLE_ID);
            if (sessionRoleId == null) {
                return new ModelAndView(UrlConfig.REDIRECT + UrlConfig.VIEW_ROLE_SELECT_REDIRECT);
            }
            ModelAndView view = new ModelAndView(UrlConfig.VIEW_WELCOME);
            // 返回支持的语言
            List<Language> list = languageProvider.getSupportedLanguages();
            view.addObject("languages",list);
            //网站标题
            view.addObject("SYS_TITLE", AccountConfig.SYS_TITLE);
            return view;
        }
        return new ModelAndView(UrlConfig.REDIRECT + UrlConfig.VIEW_LOGIN_REDIRECT);
    }
    
    private String UUID_CSRF(){
    	return UUID.randomUUID().toString();
    }
    
    @Override
    public ModelAndView loginView(User user,HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException{
    	HttpSession session = request.getSession(true);
    	if(session.getAttribute("_csrf")==null || StringUtils.isEmpty(session.getAttribute("_csrf").toString())){
    		CSRF _csrf = new CSRF("_csrf",UUID_CSRF());
    		session.setAttribute("_csrf", _csrf);
    	}
    	Locale locale = RequestContextUtils.getLocale(request);
    	
        ModelAndView view = new ModelAndView(UrlConfig.VIEW_LOGIN);
        User sessionUser = (User)session.getAttribute(User.FIELD_SESSION_USER);
        if(sessionUser ==null || sessionUser.getUserId()==null){
        	// 配置3次以后开启验证码
            Cookie cookie = WebUtils.getCookie(request, CaptchaConfig.LOGIN_KEY);
            // 向前端传递是否开启验证码
            view.addObject("ENABLE_CAPTCHA", captchaConfig.isEnableCaptcha(cookie));
            
        	// 返回支持的语言
            List<Language> list = languageProvider.getSupportedLanguages();
            view.addObject("languages",list);
        	if(StringUtil.isNull(user.getUserName()) && StringUtil.isNull(user.getPassword())){
        		return view;
        	}
        	try {
        		checkCaptcha(request, response);
        		user = userService.login(user);
			} catch (UserException e) {
	            view.addObject("msg", messageSource.getMessage(e.getCode(), null, locale));
	            
	            if (captchaConfig.getWrongTimes() > 0) {
	                if (cookie == null) {
	                    String uuid = UUID.randomUUID().toString();
	                    cookie = new Cookie(CaptchaConfig.LOGIN_KEY, uuid);
	                    cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
	                    cookie.setMaxAge(captchaConfig.getExpire());
	                    response.addCookie(cookie);
	                }
	                captchaConfig.updateLoginFailureInfo(cookie);
	            }
	            return view;
			}
        }
        captchaConfig.resetLoginFailureInfo(request,response);
    	if(StringUtil.isNotNull(user.getUserName()) && user.getUserId()!=null){
    		user.setPassword(null);
    		session.setAttribute(User.FIELD_SESSION_USER, user);
        	session.setAttribute(User.FIELD_USER_ID, user.getUserId());
            session.setAttribute(User.FIELD_USER_NAME, user.getUserName());
            session.setMaxInactiveInterval(60*60);
            
            //存储 登录用户 ip等信息
            Map<String, ISaveIpAddressListener> listeners = applicationContext.getBeansOfType(ISaveIpAddressListener.class);
            listeners.forEach((k,v) -> {
            	v.onSaveIpAdress(request, response);
            });
    	}
    	session.setAttribute(IRequest.FIELD_LOCALE, locale);
    	String token = UUID.randomUUID().toString();
    	redisTemplate.opsForValue().set("sso:session:tokenKey:"+token,objectMapper.writeValueAsString(user));
    	redisTemplate.expire("sso:session:tokenKey:"+token, 30*60, TimeUnit.SECONDS);
    	//放置token到浏览器cookie
    	CookieUtils.setCookie(request, response, "LOGIN_TOKEN", token);
    	
    	return new ModelAndView(UrlConfig.REDIRECT +UrlConfig.VIEW_ROLE_SELECT_REDIRECT);
    }

    @Override
    public ModelAndView roleView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView(UrlConfig.VIEW_ROLE_SELECT);
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 获取user
            Long userId = (Long) session.getAttribute(User.FIELD_USER_ID);
            if (userId != null) {
                User user = new User();
                user.setUserId(userId);
                session.setAttribute(User.FIELD_USER_ID, userId);
                addCookie(User.FIELD_USER_ID, userId.toString(), request, response);
                List<RoleExt> roles = roleService.selectRolesByUser(RequestHelper.createServiceRequest(request), user);
                if(roles == null || roles.isEmpty()){
                	cleanSession(request,response);
                	String msg = messageSource.getMessage(RoleException.MSG_INVALID_USER_ROLE, null, RequestContextUtils.getLocale(request));
                	mv.addObject("msg",msg);
                	mv.setViewName(UrlConfig.VIEW_LOGIN);
                	return mv;
                }
                List<Long> ids = new ArrayList<>();
                for (RoleExt roleExt : roles) {
                	ids.add(roleExt.getRoleId());
				}
                session.setAttribute(IRequest.FIELD_ALL_ROLE_ID, ids.toArray(new Long[ids.size()]));
                if(roles.size()==1){
                	session.setAttribute(Role.FIELD_ROLE_ID, roles.get(0).getRoleId());
                	mv.setViewName(UrlConfig.REDIRECT +UrlConfig.ROLE_SELECT_ED);
                	return mv;
                }
                mv.addObject("roles", roles);
            }
        }
        return mv;
    }

    protected void addCookie(String cookieName, String cookieValue, HttpServletRequest request,
            HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(StringUtils.defaultIfEmpty(request.getContextPath(), "/"));
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
    
    @Override
    public ResponseData sessionExpiredLogin(User account, HttpServletRequest request, HttpServletResponse response)
            throws RoleException {
        ResponseData data = new ResponseData();
        ModelAndView view = this.doLogin(account, request, response);
        ModelMap mm = view.getModelMap();
        if (mm.containsAttribute("code")) {
            data.setSuccess(false);
            data.setCode((String) mm.get("code"));
            data.setMessage((String) mm.get("msg"));
        } else {
            Object userIdObj = request.getParameter(User.FIELD_USER_ID);
            Object roleIdObj = request.getParameter(Role.FIELD_ROLE_ID);
            if (userIdObj != null && roleIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString()), roleId = Long.valueOf(roleIdObj.toString());
                roleService.checkUserRoleExists(request.getLocale().toString(),userId, roleId);
                HttpSession session = request.getSession();
                session.setAttribute(User.FIELD_USER_ID, userId);
                session.setAttribute(Role.FIELD_ROLE_ID, roleId);
            }
        }
        return data;
    }
    
    @Override
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response){
    	cleanSession(request,response);
    	ModelAndView view = new ModelAndView(UrlConfig.REDIRECT + UrlConfig.VIEW_LOGIN_REDIRECT);
		return view;
    }
    
    /**
     * 清除session
     * @param request
     * @param response
     */
    private void cleanSession(HttpServletRequest request, HttpServletResponse response){
    	HttpSession session = request.getSession();
		if(session.getAttribute(User.FIELD_SESSION_USER)!=null){
			session.removeAttribute(User.FIELD_SESSION_USER);
		}
		if(session.getAttribute(User.FIELD_USER_ID)!=null){
			session.removeAttribute(User.FIELD_USER_ID);
		}
		if(session.getAttribute(User.FIELD_USER_NAME)!=null){
			session.removeAttribute(User.FIELD_USER_NAME);
		}
		if(session.getAttribute(Role.FIELD_ROLE_ID)!=null){
			session.removeAttribute(Role.FIELD_ROLE_ID);
		}
		if(session.getAttribute(Role.FIELD_ROLE_NAME)!=null){
			session.removeAttribute(Role.FIELD_ROLE_NAME);
		}
		session.invalidate();
    }
}