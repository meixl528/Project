package com.ssm.function.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.cache.impl.ResourceItemCache;
import com.ssm.cache.impl.RoleResourceItemCache;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.Resource;
import com.ssm.function.dto.ResourceItem;
import com.ssm.function.mapper.ResourceItemMapper;
import com.ssm.function.mapper.RoleResourceItemMapper;
import com.ssm.function.service.IResourceItemService;
import com.ssm.sys.service.impl.BaseServiceImpl;

@Transactional
@Service
public class ResourceItemServiceImpl extends BaseServiceImpl<ResourceItem> implements IResourceItemService {

    @Autowired
    private ResourceItemMapper resourceItemMapper;
    @Autowired
    private RoleResourceItemMapper roleResourceItemMapper;
    @Autowired
    private ResourceItemCache resourceItemCache;
    @Autowired
    private RoleResourceItemCache roleResourceItemCache;

    /**
     * 新增资源组件.
     *
     * @param request      上下文请求
     * @param resourceItem 资源组件
     * @return 返回新增后的资源组件信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceItem insertSelective(IRequest request, ResourceItem resourceItem) {
        if (resourceItem == null) {
            return null;
        }
        super.insertSelective(request, resourceItem);
        resourceItemCache.load(resourceItem.getOwnerResourceId().toString());
        return resourceItem;
    }

    /**
     * 资源组件修改.
     *
     * @param request      上下文请求
     * @param resourceItem 资源组件
     * @return 修改后的资源组件信息
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceItem updateByPrimaryKey(IRequest request, ResourceItem resourceItem) {
        if (resourceItem == null) {
            return null;
        }
        super.updateByPrimaryKey(request, resourceItem);
        resourceItemCache.load(resourceItem.getOwnerResourceId().toString());
        return resourceItem;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ResourceItem> selectResourceItems(IRequest request, Resource resource) {
        return resourceItemMapper.selectResourceItemsByResourceId(resource);
    }

    @Override
    public List<ResourceItem> batchUpdate(IRequest request, List<ResourceItem> resourceItems) {
        if (resourceItems == null || resourceItems.isEmpty()) {
            return resourceItems;
        }
        for (ResourceItem resourceItem : resourceItems) {
            if (resourceItem.getResourceItemId() == null) {
                self().insertSelective(request, resourceItem);
            } else {
                self().updateByPrimaryKey(request, resourceItem);
            }
        }
        return resourceItems;
    }

    @Override
    public void batchDelete(IRequest requestContext, List<ResourceItem> resourceItems) {
        Long roleId = requestContext.getRoleId();
        if (resourceItems == null || resourceItems.isEmpty()) {
            return;
        }
        for (ResourceItem resourceItem : resourceItems) {
            if (resourceItem == null || resourceItem.getResourceItemId() == null) {
                return;
            }
            resourceItemMapper.deleteByPrimaryKey(resourceItem);
            roleResourceItemMapper.deleteByResourceItemId(resourceItem.getResourceItemId());
            resourceItemCache.load(resourceItem.getOwnerResourceId().toString());
            roleResourceItemCache.load(roleId.toString());
        }
    }

    @Override
    public ResourceItem selectResourceItemByResourceIdAndItemId(ResourceItem resourceItem) {
        return resourceItemMapper.selectResourceItemByResourceIdAndItemId(resourceItem);
    }

}