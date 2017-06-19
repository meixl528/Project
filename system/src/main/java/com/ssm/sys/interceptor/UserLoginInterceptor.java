package com.ssm.sys.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ssm.account.dto.User;
import com.ssm.adaptor.UrlUtil;
/**
 * 拦截请求 ,
 * 检测session中是否有用户信息
 * @author meixl	2017年3月31日下午2:29:32
 */
public class UserLoginInterceptor implements HandlerInterceptor {
	//设置允许通过的url
	private List<String> allowedUrl = new ArrayList<>();
	public void setAllowedUrl(List<String> url) {
		if(!allowedUrl.contains(UrlUtil.VIEW_LOGIN)){
			allowedUrl.add(UrlUtil.VIEW_LOGIN);
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
		resp.sendRedirect(req.getContextPath() + UrlUtil.VIEW_LOGIN);
		return false;
	}

	// 在请求的方法执行之后执行
	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object obj, ModelAndView mv)
			throws Exception {
	}

	// 在DispatCherServlet 处理之后执行 ----清理工作
	@Override
	public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object obj, Exception e)
			throws Exception {
	}
	
	/**
	 * 跨站请求伪造(Cross Site Request Forgery)
	 * @param 官方建议引用   spring security 
	 */
	private boolean csrf(HttpServletRequest req){
		Map<String, String[]> map = req.getParameterMap();
		Object _csrf = req.getParameter("_csrf");
		if(_csrf!=null){
			Object csrf = req.getSession(true).getAttribute("_csrf");
			if(csrf==null || !csrf.equals(_csrf)){
				return false;
			}
		}
		return true;
	}

}
