package com.ssm.attachment.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssm.attachment.dto.AttachCategory;
import com.ssm.mybatis.common.Mapper;

public interface AttachCategoryMapper extends Mapper<AttachCategory> {

    /**
     * 根据参数查找AttachCategory.
     * 
     * @param attachCategory
     *            AttachCategory对象
     * @return AttachCategory 插入的AttachCategory对象
     */
    AttachCategory selectCategory(AttachCategory attachCategory);

    /**
     * 查询所有的SourceTypes 不重复.
     * 
     * @return List SourceType的List
     */
    List<String> selectAllSourceTypes();

    /**
     * 查询父节点目录.
     * 
     * @param idList
     * @return List
     */
    List<AttachCategory> selectAllParentCategory(List<Long> idList);

    /*
     * 查询所有节点
     */
    List<AttachCategory> queryTree(AttachCategory attachCategory);

    /* 验证type和size */
    List<AttachCategory> querySize(@Param("sourceType") String sourceType, @Param("sourceKey") String sourceKey);
}
