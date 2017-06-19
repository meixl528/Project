package com.ssm.function.service;

import java.util.List;

import com.ssm.core.annotation.StdWho;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.MenuItem;
import com.ssm.function.dto.Resource;
import com.ssm.function.dto.RoleResourceItem;
/**
 * @name        IRoleResourceItemService
 * @description 
 * @author      meixl
 * @date        2017年5月9日下午1:30:30
 * @version
 */
public interface IRoleResourceItemService extends ProxySelf<IRoleResourceItemService> {
    /**
     * 查询角色资源下未获得授权的组件id
     *
     * @param resource
     * @param roleId
     * @return list
     */
    List<String> getNotAccessItems(Resource resource, Long roleId);

    /**
     * 构建资源组件菜单以及勾选角色拥有的资源组件项.
     *
     * @param requestContext
     * @param roleId
     * @param functionId
     * @return
     */
    List<MenuItem> queryMenuItems(IRequest requestContext, Long roleId, Long functionId);

    /**
     * 查询角色拥有的权限项.
     *
     * @param requestContext
     * @param roleId
     * @param functionId
     * @return List
     */
    List<RoleResourceItem> queryRoleResourceItems(IRequest requestContext, Long roleId, Long functionId);


    /**
     * 保存角色拥有的权限项.
     *
     * @param requestContext
     * @param roleResourceItems
     * @param roleId
     * @param functionId
     * @return
     */
    List<RoleResourceItem> batchUpdate(IRequest requestContext, @StdWho List<RoleResourceItem> roleResourceItems,
                                       Long roleId, Long functionId);

    /**
     * 判断是否拥有权限项.
     *
     * @param roleId
     * @param resourceItemId
     * @return boolean
     */
    boolean hasResourceItem(Long roleId, Long resourceItemId);


}
