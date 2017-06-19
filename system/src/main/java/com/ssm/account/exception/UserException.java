/*
 * #{copyright}#
 */
package com.ssm.account.exception;

import com.ssm.core.exception.BaseException;
/**
 * @name UserException
 * @author meixl	2017年3月30日下午5:00:46
 */
public class UserException extends BaseException {

    private static final long serialVersionUID = -3250576758107608016L;

    // 验证码不正确
    public static final String ERROR_INVALID_CAPTCHA = "error.login.verification_code_error";

    // 用户名密码不匹配
    public static final String ERROR_USER_PASSWORD = "error.login.name_password_not_match";

    // 账户状态未激活
    public static final String ERROR_USER_NOT_ACTIVE = "error.user.account_not_active";

    // 账户失效
    public static final String ERROR_USER_EXPIRED = "error.user.account_expired";

    // 账户锁定
    public static final String ERROR_USER_LOCKED = "error.user.account_locked";


    // 用户密码错误超过一定次数锁定一段时间
    public static final String MSG_LOGIN_ACCOUNT_LOCK_MAX_RETRY_TIMES = "error.mws.login.account_lock_max_retry_times";

    private static final String EXCEPTION_CODE = "sys.user";

    /**
     * 用户管理 - 登录名被占用.
     */
    public static final String USER_EXIST = "error.user.um_exist_already";

    /**
     * 用户管理 - 登录名格式错误.
     */
    public static final String USER_FORMAT = "error.user.user_format";
    /**
     * 用户管理 - 手机被占用.
     */
    public static final String PHONE_EXIST = "error.user.phone_exist_already";
    /**
     * 用户管理 - 手机不存在.
     */
    public static final String PHONE_NOT_EXIST = "error.user.phone_not_exist";
    /**
     * 用户管理 - 手机不能为空.
     */
    public static final String PHONE_NOT_ISEMPTY = "error.user.phone_not_isempty";
    /**
     * 用户管理 - 手机格式错误.
     */
    public static final String PHONE_FORMAT = "error.user.phone_format";
    /**
     * 用户管理 - 邮箱被占用.
     */
    public static final String EMAIL_EXIST = "error.user.email_exist";
    /**
     * 用户管理 - 邮箱不存在.
     */
    public static final String EMAIL_NOT_EXIST = "error.user.email_not_exist";
    /**
     * 用户管理 - 邮箱不能为空.
     */
    public static final String EMAIL_NOT_ISEMPTY = "error.user.email_not_isempty";
    /**
     * 用户管理 - 邮箱格式错误.
     */
    public static final String EMAIL_FORMAT = "error.user.email_format";
    /**
     * 密码管理 - 忘记密码 - 验证码出错.
     */
    public static final String LOGIN_VERIFICATION_CODE_ERROR = "error.login.verification_code_error";
    /**
     * 密码管理 - 忘记密码 - 校验码出错.
     */
    public static final String CAPTCHA_ERROR = "error.captcha_error";
    /**
     * 密码管理 - 忘记密码 - 校验码验证方式出错.
     */
    public static final String CAPTCHA_WAY_ERROR = "error.captcha_error_wrong_way";
    /**
     * 密码管理 - 忘记密码 - 请先发送校验码.
     */
    public static final String NOT_SENT_CAPTCHA = "error.system.not_sent_captcha";
    /**
     * 密码管理 - 忘记密码 - 请输入验证码.
     */
    public static final String ENTER_VERIFICATION = "error.system.enter_verification";
    /**
     * 密码管理 - 忘记密码 - 请输入校验码.
     */
    public static final String ENTER_CAPTCHA = "error.system.enter_captcha";
    /**
     * 密码管理 - 忘记密码 - 校验码已过期.
     */
    public static final String CAPTCHA_EXPIRED = "error.system.captcha_expired";
    /**
     * 密码管理 - 修改密码 - 两次密码不能一致.
     */
    public static final String USER_PASSWORD_NOT_SAME_TWICE = "error.password.not_same_twice";
    /**
     * 密码管理 - 修改密码 - 当前密码校验.
     */
    public static final String USER_PASSWORD_WRONG = "error.password.current_password";
    /**
     * 密码管理 - 修改密码 - 密码格式不正确.
     */
    public static final String USER_PASSWORD_REQUIREMENT = "error.user.password_format_error";
    /**
     * 密码管理 - 修改密码 - 新密码不能与旧密码一致.
     */
    public static final String USER_PASSWORD_SAME = "error.system.not_allowed_same_with_old_password";
    /**
     * 用户管理 - 个人信息 - 用户名不存在.
     */
    public static final String USER_NOT_EXIST = "error.system.user_not_exist";
    /**
     * 用户管理 - 个人信息 - 用户已失效.
     */
    public static final String USER_EXPIRED = "error.system.user_expired";
    /**
     * 用户管理 - 个人信息 - 创建用户失败.
     */
    public static final String USER_INSERT_FAIL = "error.system.user_insert_fail";
    /**
     * 用户管理 - 个人信息 - 更新用户失败.
     */
    public static final String USER_UPDATE_FAIL = "error.system.user_update_fail";
    /**
     * 用户管理 - 密码不能为空.
     */
    public static final String PASSWORD_NOT_EMPTY = "error.user.password_not_empty";
    /**
     * 用户管理 - 临时口令已失效.
     */
    public static final String TEMP_PASSWORD_EXPIRED = "error.user.temp_password_expired";
    /**
     * 密码管理 - 忘记密码 - 请输入用户名.
     */
    public static final String ENTER_USERNAME = "error.system.enter_username";
    /**
     * logger-日期格式捕获异常.
     */
    public static final String DATE_FORMAT = "error.integration.dapp.date.format.failed";

    public UserException(String code, String message, Object[] parameters) {
        super(code, message, parameters);
    }

    public UserException(String message, Object[] parameters) {
        super(EXCEPTION_CODE, message, parameters);
    }

}
