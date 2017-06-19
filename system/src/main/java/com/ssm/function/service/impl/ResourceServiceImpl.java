/*
 * #{copyright}#
 */
package com.ssm.function.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ssm.cache.Cache;
import com.ssm.cache.CacheManager;
import com.ssm.core.BaseConstants;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.Resource;
import com.ssm.function.mapper.FunctionResourceMapper;
import com.ssm.function.mapper.ResourceMapper;
import com.ssm.function.service.IResourceService;

/**
 * @author wuyichu
 */
@Transactional
@Service
public class ResourceServiceImpl implements IResourceService {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private FunctionResourceMapper functionResourceMapper;

    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Resource selectResourceByUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        Cache<Resource> cache = getResourceByURL();
        Resource resource = cache.getValue(url);
        if (resource == null) {
            resource = resourceMapper.selectResourceByUrl(url);
            if (resource != null) {
                flushCache(resource);
            }
        }
        return resource;
    }

  
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Resource selectResourceById(Long id) {
        if (id == null) {
            return null;
        }
        Cache<Resource> cache = getResourceById();
        Resource resource = cache.getValue(id.toString());
        if (resource == null) {
            resource = resourceMapper.selectByPrimaryKey(id);
            flushCache(resource);
        }
        return resource;
    }

    @Override
    public Resource insertSelective(IRequest request, Resource resource) {
        if (StringUtils.isEmpty(resource.getUrl())) {
            return null;
        }
        resourceMapper.insertSelective(resource);
        flushCache(resource);
        return resource;
    }

    @SuppressWarnings("unchecked")
	private Cache<Resource> getResourceByURL() {
        return (Cache<Resource>) cacheManager.getCache(BaseConstants.CACHE_RESOURCE_URL);
    }

    @SuppressWarnings("unchecked")
	private Cache<Resource> getResourceById() {
        return (Cache<Resource>) cacheManager.getCache(BaseConstants.CACHE_RESOURCE_ID);
    }

    private void flushCache(Resource resource) {
        if (resource == null) {
            return;
        }

        Cache<Resource> resourceCache = getResourceByURL();
        resourceCache.setValue(resource.getUrl(), resource);
        Cache<Resource> resourceCache2 = getResourceById();
        resourceCache2.setValue(resource.getResourceId().toString(), resource);
    }

    private void removeCache(Resource resource) {
        if (resource == null) {
            return;
        }
        Cache<Resource> resourceCache = getResourceByURL();
        resourceCache.remove(resource.getUrl());
        Cache<Resource> resourceCache2 = getResourceById();
        resourceCache2.remove(resource.getResourceId().toString());
    }

    
    @Override
    public Resource selectByPrimaryKey(IRequest request, Resource resource)  {
        Resource old = resourceMapper.selectByPrimaryKey(resource.getResourceId());
        removeCache(old);
        resourceMapper.updateByPrimaryKeySelective(resource);
        flushCache(resource);
        return resource;
    }

    @Override
    public List<Resource> batchUpdate(IRequest requestContext, List<Resource> resources)  {
        for (Resource resource : resources) {
            if (resource.getResourceId() != null) {
                self().updateByPrimaryKeySelective(requestContext, resource);
            } else {
                self().insertSelective(requestContext, resource);
            }
        }
        return resources;
    }

    @Override
    public Resource updateByPrimaryKeySelective(IRequest request, Resource record) {
        resourceMapper.updateByPrimaryKeySelective(record);
        flushCache(record);
        return record;
    }

    @Override
    public int deleteByPrimaryKey(Resource resource)  {
        if (resource == null || resource.getResourceId() == null || StringUtils.isEmpty(resource.getUrl())) {
            return 0;
        }
        functionResourceMapper.deleteByResource(resource);
        resourceMapper.deleteByPrimaryKey(resource);
        removeCache(resource);
        return 1;
    }

    @Override
    public void batchDelete(IRequest requestContext, List<Resource> resources)  {
        if (resources == null || resources.isEmpty()) {
            return;
        }
        for (Resource resource : resources) {
            self().deleteByPrimaryKey(resource);
        }
    }

}
