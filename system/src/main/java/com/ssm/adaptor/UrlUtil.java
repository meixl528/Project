package com.ssm.adaptor;

public interface UrlUtil {

	// 验证码改成可配置, 验证码打开
    static final boolean VALIDATE_CAPTCHA = true;

    // 跳转
    static final String REDIRECT = "redirect:";

    // 校验码
    static final String KEY_VERIFICODE = "verifiCode";

    // 默认主页
    static final String VIEW_WELCOME = "/welcome.html";

    // 默认的登录页
    static final String VIEW_LOGIN = "/login.html";

    // 默认角色选择路径
    static final String VIEW_ROLE_SELECT = "/role.html";
    
    // 选择角色
    static final String ROLE_SELECT_ED = "/selectedRole.html";
    
    //后缀
    static final String URL_SUFFIX = ".html";
	
}
