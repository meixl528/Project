package com.ssm.adaptor;

public interface UrlConfig{

    // 跳转
    public static final String REDIRECT = "redirect:";

    // 校验码
    public static final String KEY_VERIFICODE = "verifiCode";

    // 默认主页
    public static final String VIEW_WELCOME = "/welcome";
    public static final String VIEW_WELCOME_REDIRECT = "/welcome.html";

    // 默认的登录页
    public static final String VIEW_LOGIN = "/login";
    public static final String VIEW_LOGIN_REDIRECT = "/login.html";

    // 默认角色选择路径
    public static final String VIEW_ROLE_SELECT = "/role";
    public static final String VIEW_ROLE_SELECT_REDIRECT = "/role.html";
    
    // 选择角色
    public static final String ROLE_SELECT_ED = "/selectedRole";
    
    //后缀
    public static final String SUFFIX = ".html";
	
}
