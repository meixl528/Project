package com.ssm.account.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.account.dto.User;
import com.ssm.account.dto.UserRole;
import com.ssm.account.mapper.UserRoleMapper;
import com.ssm.account.service.IRole;
import com.ssm.account.service.IRoleService;
import com.ssm.account.service.IUserRoleService;
import com.ssm.core.request.IRequest;
import com.ssm.sys.service.impl.BaseServiceImpl;
/**
 * @name        UserRoleServiceImpl
 * @description 
 * @author      meixl
 * @date        2017年5月8日下午4:01:43
 * @version
 */
@Service
public class UserRoleServiceImpl extends BaseServiceImpl<UserRole> implements IUserRoleService {

    private Logger logger = LoggerFactory.getLogger(UserRoleServiceImpl.class);

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    //@Qualifier("roleServiceImpl")
    private IRoleService roleService;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<IRole> selectUserRoles(IRequest requestContext, UserRole userRole) {
        User u = new User();
        u.setUserId(userRole.getUserId());
        return roleService.selectRolesByUser(requestContext, u);
        // List<Role> selectUserRoles =
        // roleMapper.selectUserRoles(role.getUserId());
        // return selectUserRoles;
    }

    @Override
    public int deleteByPrimaryKey(UserRole record) {
        if (record.getSurId() != null)
            return super.deleteByPrimaryKey(record);
        return userRoleMapper.deleteByRecord(record);
    }

}
