package com.ssm.function.mapper;

import java.util.List;

import com.ssm.function.dto.Function;
import com.ssm.function.dto.Resource;
import com.ssm.function.dto.ResourceItem;

import com.ssm.mybatis.common.Mapper;
/**
 * @name        ResourceItemMapper
 * @description 
 * @author      meixl
 * @date        2017年5月9日下午1:35:30
 * @version
 */
public interface ResourceItemMapper extends Mapper<ResourceItem> {

    List<ResourceItem> selectResourceItemsByResourceId(Resource resource);

    List<ResourceItem> selectResourceItemsByFunctionId(Function function);

    ResourceItem selectResourceItemByResourceIdAndItemId(ResourceItem record);

    List<ResourceItem> selectForCache();
}
