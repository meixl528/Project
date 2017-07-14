package com.ssm.account.service;

import java.util.List;

import com.ssm.account.dto.RoleExt;
import com.ssm.account.dto.UserRole;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.sys.service.IBaseService;

/**
 * @name        IUserRoleService
 * @description 
 * @author      meixl
 * @date        2017年5月8日下午3:59:25
 * @version
 */
public interface IUserRoleService extends IBaseService<UserRole>,ProxySelf<IUserRoleService>{

	/**
	 * 查询用户角色
	 * @param requestContext
	 * @param example
	 * @return
	 */
	List<RoleExt> selectUserRoles(IRequest requestContext, UserRole userRole);

}
