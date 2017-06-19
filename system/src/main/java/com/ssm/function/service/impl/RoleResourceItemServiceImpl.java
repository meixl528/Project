package com.ssm.function.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.cache.impl.ResourceItemCache;
import com.ssm.cache.impl.RoleResourceItemCache;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.Function;
import com.ssm.function.dto.MenuItem;
import com.ssm.function.dto.Resource;
import com.ssm.function.dto.ResourceItem;
import com.ssm.function.dto.RoleResourceItem;
import com.ssm.function.mapper.FunctionMapper;
import com.ssm.function.mapper.ResourceItemMapper;
import com.ssm.function.mapper.RoleResourceItemMapper;
import com.ssm.function.service.IRoleResourceItemService;
/**
 * @name        RoleResourceItemServiceImpl
 * @description 
 * @author      meixl
 * @date        2017年5月9日下午1:31:41
 */
@Transactional
@Service
public class RoleResourceItemServiceImpl implements IRoleResourceItemService {

    @Autowired
    private ResourceItemMapper resourceItemMapper;
    @Autowired
    private RoleResourceItemMapper roleResourceItemMapper;
    @Autowired
    private FunctionMapper functionMapper;
    @Autowired
    private ResourceItemCache resourceItemCache;
    @Autowired
    private RoleResourceItemCache roleResourceItemCache;

    @Override
    public List<String> getNotAccessItems(Resource resource, Long roleId) {
        ResourceItem[] resourceItems = resourceItemCache.getValue(resource.getResourceId().toString());
        Long[] roleResourceItemIds = roleResourceItemCache.getValue(roleId.toString());
        List<String> notAccessItems = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(resourceItems)) {
            for (ResourceItem resourceItem : resourceItems) {
                boolean notAccessItem = true;
                if (ArrayUtils.isNotEmpty(roleResourceItemIds)) {
                    for (Long roleResourceItemId : roleResourceItemIds) {
                        if (roleResourceItemId.equals(resourceItem.getResourceItemId())) {
                            notAccessItem = false;
                            break;
                        }
                    }
                }
                if (notAccessItem) {
                    notAccessItems.add(resourceItem.getItemId());
                }
            }
        }
        return notAccessItems;
    }


    /**
     * 查询功能下所有html资源
     *
     * @param function
     * @return
     */
    private List<Resource> queryHtmlResources(Function function) {
        Resource resource = new Resource();
        resource.setType("HTML");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("function", function);
        params.put("resource", resource);
        return functionMapper.selectExistsResourcesByFunction(params);
    }

    /**
     * 查询资源组件菜单
     *
     * @param requestContext
     * @param roleId
     * @param functionId
     * @return
     */
    @Override
    public List<MenuItem> queryMenuItems(IRequest requestContext, Long roleId, Long functionId) {
        Function function = new Function();
        function.setFunctionId(functionId);
        Long[] roleResourceItemIds = null;
        if (roleId != null) {
            roleResourceItemIds = roleResourceItemCache.getValue(roleId.toString());
        }
        return castResourcesToMenuItem(queryHtmlResources(function), roleResourceItemIds);
    }

    /**
     * 构建资源组件菜单
     *
     * @param resourceList
     * @param roleResourceItemIds    角色拥有的资源组件权限
     * @return 资源组件菜单
     */
    private List<MenuItem> castResourcesToMenuItem(List<Resource> resourceList, Long[] roleResourceItemIds) {
        MenuItem root = new MenuItem();
        List<MenuItem> children = new ArrayList<>();
        root.setChildren(children);
        if (resourceList != null) {
            for (Resource resource : resourceList) {
                MenuItem menuItem = createMenuItem(resource, roleResourceItemIds);
                if (menuItem != null)
                    children.add(menuItem);
            }
        }
        return root.getChildren();
    }

    /**
     * 构建资源节点 将角色拥有的资源组件节点打勾
     *
     * @param resource
     * @param roleResourceItemIds
     * @return null:资源下没有资源组件 menu：资源节点
     */
    private MenuItem createMenuItem(Resource resource, Long[] roleResourceItemIds) {
        ResourceItem[] resourceItems = resourceItemCache.getValue(resource.getResourceId().toString());
        int size = 0;
        if (ArrayUtils.isNotEmpty(resourceItems)) {
            MenuItem menu = new MenuItem();
            List<MenuItem> children = new ArrayList<>();
            menu.setChildren(children);
            menu.setText(resource.getName());
            menu.setUrl(resource.getUrl());
            menu.setId(resource.getResourceId());
            for (ResourceItem resourceItem : resourceItems) {
                MenuItem menuItem = createMenuItem(resourceItem);
                //判断角色是否拥有组件权限 有则勾选相应组件
                if (ArrayUtils.isNotEmpty(roleResourceItemIds)) {
                    for (Long roleResourceItemId : roleResourceItemIds) {
                        if (roleResourceItemId.equals(resourceItem.getResourceItemId())) {
                            menuItem.setIschecked(Boolean.TRUE);
                            size++;
                            break;
                        }
                    }
                }
                children.add(menuItem);
            }
            if (size == resourceItems.length) {
                menu.setIschecked(Boolean.TRUE);
            }
            return menu;
        } else {
            return null;
        }
    }

    /**
     * 构建资源组件节点
     *
     * @param resourceItem
     * @return
     */
    private MenuItem createMenuItem(ResourceItem resourceItem) {
        MenuItem menu = new MenuItem();
        menu.setText(resourceItem.getItemName());
        menu.setUrl(resourceItem.getDescription());
        menu.setId(resourceItem.getResourceItemId());
        return menu;
    }

    @Override
    public List<RoleResourceItem> queryRoleResourceItems(IRequest requestContext, Long roleId, Long functionId) {
        Function f = new Function();
        f.setFunctionId(functionId);
        List<RoleResourceItem> result = new ArrayList<>();
        List<RoleResourceItem> roleItems = roleResourceItemMapper.selectResourceItemsByRole(roleId);
        List<ResourceItem> resourceItems = resourceItemMapper.selectResourceItemsByFunctionId(f);
        for (ResourceItem item : resourceItems) {
            RoleResourceItem rri = new RoleResourceItem();
            rri.setResourceItemId(item.getResourceItemId());
            rri.setTargetResourceName(item.getTargetResourceName());
            rri.setOwnerResourceId(item.getOwnerResourceId());
            rri.setTargetResourceId(item.getTargetResourceId());
            rri.setItemId(item.getItemId());
            rri.setItemName(item.getItemName());
            rri.setDescription(item.getDescription());
            for (RoleResourceItem roleItem : roleItems) {
                if (roleItem.getResourceItemId().equals(item.getResourceItemId())) {
                    rri.setRsiId(roleItem.getRsiId());
                    break;
                }
            }
            result.add(rri);
        }
        return result;
    }

    @Override
    public List<RoleResourceItem> batchUpdate(IRequest requestContext, List<RoleResourceItem> roleResourceItems,
                                              Long roleId, Long functionId) {
        roleResourceItemMapper.deleteByRoleIdAndFunctionId(roleId, functionId);
        for (RoleResourceItem roleResourceItem : roleResourceItems) {
            roleResourceItemMapper.insert(roleResourceItem);
            roleResourceItemCache.load(roleId.toString());
        }
        return roleResourceItems;
    }

    @Override
    public boolean hasResourceItem(Long roleId, Long resourceItemId) {
        if (roleId == null || resourceItemId == null) {
            return false;
        }
        return roleResourceItemMapper.selectByRoleIdAndResourceItemId(roleId, resourceItemId) != null;
    }

}
