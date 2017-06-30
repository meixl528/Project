/*
 * #{copyright}#
 */
package com.ssm.function.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.function.dto.Resource;
import com.ssm.sys.service.IBaseService;

/**
 * @author meixl
 */
public interface IResourceService extends IBaseService<Resource>, ProxySelf<IResourceService> {

   /**
    * 根据资源的url查询资源数据.
    * @param url url
    * @return Resource
    */
    Resource selectResourceByUrl(String url);

    @Transactional(propagation = Propagation.SUPPORTS)
    Resource selectResourceById(Long id);

    /**
     * 批量修改或新增资源记录.
     * @param requestContext requestContext
     * @param resources resources
     * @return Resource
     */
    List<Resource> batchUpdate(IRequest requestContext,List<Resource> resources);

    /**
     * 批量删除资源记录.
     * 
     * @param requestContext requestContext
     * @param resources resources
     */
    void batchDelete(IRequest requestContext, List<Resource> resources);

    
	Resource insertSelective(IRequest request, Resource resource);
	Resource selectByPrimaryKey(IRequest request, Resource resource);
	Resource updateByPrimaryKeySelective(IRequest requestContext, Resource resource);
	int deleteByPrimaryKey(Resource resource);

}
