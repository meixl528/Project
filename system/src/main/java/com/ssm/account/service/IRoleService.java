package com.ssm.account.service;

import java.util.List;

import com.ssm.account.dto.Role;
import com.ssm.account.dto.RoleExt;
import com.ssm.account.dto.User;
import com.ssm.account.exception.RoleException;
import com.ssm.core.annotation.StdWho;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.sys.service.IBaseService;

public interface IRoleService extends IBaseService<Role>, ProxySelf<IRoleService>{

	List<RoleExt> selectRolesByUser(IRequest requestContext, User user);

	Role checkUserRoleExists(String locale,Long userId, Long roleId) throws RoleException;
	
	/**
	 * 删除角色 
	 * @param list
	 */
	void deleteRole(List<Role> list);
	
	/**
	 * 保存角色
	 * @param requestContext
	 * @param list
	 */
	List<Role> submitRole(IRequest requestContext,@StdWho List<Role> list);

	/**
	 * 查询角色
	 * @param requestContext
	 * @param role
	 * @param page
	 * @param pagesize
	 * @return
	 */
	List<Role> selectRole(IRequest requestContext, Role role, int page, int pagesize);

	/**
	 * 查询用户没有的角色
	 * @param requestContext
	 * @param roleExt
	 * @param page
	 * @param pagesize
	 * @return
	 */
	List<Role> selectRoleNotUserRoles(IRequest requestContext, Role roleExt, int page, int pagesize);

}
