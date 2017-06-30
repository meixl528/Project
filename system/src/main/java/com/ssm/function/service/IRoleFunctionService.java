/*
 * #{copyright}#
 */
package com.ssm.function.service;

import java.util.List;

import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.RoleFunction;
import com.ssm.sys.service.IBaseService;

/**
 * @author meixl
 */
public interface IRoleFunctionService extends IBaseService<RoleFunction>, ProxySelf<IRoleFunctionService> {

    /**
     * 保存角色分配的功能.
     * 
     * @param requestContext requestContext
     * @param rolefunctions functions
     * @return list
     */
    List<RoleFunction> batchUpdate(IRequest requestContext,List<RoleFunction> rolefunctions);

    /**
     * 从缓存中查询角色的所有功能ID的集合. 
     * @param roleId 角色id
     * @return roleFunction
     */
    Long[] getRoleFunctionById(Long roleId);
    
    
    /**
     * 清空角色功能.
     * 
     * @param roleId 角色id
     */
    void clearRoleFunctionByRoleId(Long roleId);
    
    /**
     * 重新加载角色资源缓存.
     * 
     * @param roleId 角色id
     */
    void reloadRoleResourceCache(Long roleId);
}
