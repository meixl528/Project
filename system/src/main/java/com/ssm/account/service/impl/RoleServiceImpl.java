package com.ssm.account.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.account.dto.Role;
import com.ssm.account.dto.RoleExt;
import com.ssm.account.dto.User;
import com.ssm.account.exception.RoleException;
import com.ssm.account.mapper.RoleMapper;
import com.ssm.account.service.IRoleService;
import com.ssm.core.request.IRequest;
import com.ssm.sys.service.impl.BaseServiceImpl;
/**
 * @name        RoleServiceImpl
 * @description 
 * @author      meixl
 * @date        2017年5月8日下午1:39:34
 * @version
 */
@Service
@Transactional
public class RoleServiceImpl extends BaseServiceImpl<Role> implements IRoleService {

	@Autowired
	private RoleMapper roleMapper;
	
	@Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<RoleExt> selectRolesByUser(IRequest requestContext, User user) {
		return roleMapper.selectByUser(user);
    }
	
	@Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Role checkUserRoleExists(String locale,Long userId, Long roleId) throws RoleException {
		List<Role> list = roleMapper.selectUserRoleCount(locale,userId, roleId);
        if (list.size() != 1) {
            throw new RoleException(RoleException.MSG_INVALID_USER_ROLE, RoleException.MSG_INVALID_USER_ROLE, null);
        }
        return list.get(0);
    }
	
	/**
	 * 删除角色
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteRole(List<Role> list){
		for (Role role: list) {
			roleMapper.deleteByPrimaryKey(role);
		}
	}

	/**
	 * 保存角色
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public List<Role> submitRole(IRequest requestContext,List<Role> list){
		for (Role role : list) {
			if(role.getRoleId()==null){
				roleMapper.insertSelective(role);
				//self().insertRole(requestContext,role);
			}else{
				roleMapper.updateByPrimaryKey(role);
			}
		}
		return list;
	}

	/**
	 * 查询角色
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<Role> selectRole(IRequest requestContext, Role role, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		return roleMapper.selectRole(role);
	}

	/**
	 * 查询用户没有的角色
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<Role> selectRoleNotUserRoles(IRequest requestContext, Role roleExt, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		return roleMapper.selectRoleNotUserRoles(roleExt);
	}
}
