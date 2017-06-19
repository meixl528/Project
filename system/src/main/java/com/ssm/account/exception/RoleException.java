/*
 * #{copyright}#
 */
package com.ssm.account.exception;

import com.ssm.core.exception.BaseException;

/**
 * @name RoleException
 * @author meixl	2017年3月30日下午5:01:18
 */
public class RoleException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 账号角色不存在.
     */
    public static final String MSG_INVALID_USER_ROLE = "mws.error.account_role_invalid";
    
    /**
     * 非法参数.
     */
    public static final String MSG_INVALID_PARAMETER = "mws.error.invalid_parameter";

    public RoleException(String code, String message, Object[] parameters) {
        super(code, message, parameters);
    }

}
