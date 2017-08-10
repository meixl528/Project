package com.ssm.account.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ssm.message.profile.SystemConfigListener;

/**
 * @name        UserConfig
 * @description 密码规则配置
 * @author      meixl
 * @date        2017年8月8日下午6:14:01
 * @version
 */
@Component
public class AccountConfig implements SystemConfigListener{
	//网站标题
	public static String SYS_TITLE = "FBI";  
	
	//密码规则配置
	public static String DEFAULT_PASSWORD = "123456";         //默认密码 123456
	public static int PASSWORD_MIN_LENGTH = 8;      //密码最小长度:8
	public static String PASSWORD_COMPLEXITY ="no_limit";      //密码复杂度:无限制
	
	@Override
	public List<String> getAcceptedProfiles() {
		return Arrays.asList("SYS_TITLE","DEFAULT_PASSWORD","PASSWORD_MIN_LENGTH","PASSWORD_COMPLEXITY");
	}

	@Override
	public void updateProfile(String profileName, String profileValue) {
		if(profileName.equals("SYS_TITLE")){
			SYS_TITLE = profileValue;
		}else if(profileName.equals("DEFAULT_PASSWORD")){
			DEFAULT_PASSWORD = profileValue;
		}else if(profileName.equals("PASSWORD_MIN_LENGTH")){
			PASSWORD_MIN_LENGTH = Integer.valueOf(profileValue);
		}else if(profileName.equals("PASSWORD_COMPLEXITY")){
			PASSWORD_COMPLEXITY = profileValue;
		}
	}

	/**
	 * 用户名已存在    ex : 用户名:{0}已存在!
	 */
	public static final String USERNAME_EXIST = "UserName_Exist";
	
	/**
     * 用户管理 - 密码格式校验.
     */
    public static final String PASSWORD_FORMAT = "^(?![^a-zA-Z]+$)(?!\\D+$)[a-zA-Z0-9._`~!@#$%^&*()+-={}:;<>?,\\\\\"\'\\[\\]]{8,}$";
    
    /**
     * 正则表达式-loginName.
     */
    public static final String LOGIN_NAME_REGEX = "^[A-Za-z0-9]{6,20}$";
    /**
     * 正则表达式-userName.
     */
    public static final String USER_NAME_REGEX = "^[\\s\\S]{1,20}$";
    /**
     * 正则表达式-phone.
     */
    public static final String PHONE_REGEX = "^1[3|4|5|8][0-9]\\d{4,8}";
    /**
     * 正则表达式-email.
     */
    public static final String EMAIL_REGEX = "^([\\s\\S]*)+@([\\S\\s]*)+(\\.([\\S\\s]*)+)+$";
}
