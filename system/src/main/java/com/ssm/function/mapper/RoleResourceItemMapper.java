package com.ssm.function.mapper;

import java.util.List;

import com.ssm.function.dto.RoleResourceItem;

import com.ssm.mybatis.common.Mapper;
/**
 * @name        RoleResourceItemMapper
 * @description 
 * @author      meixl
 * @date        2017年5月9日下午1:36:21
 */
public interface RoleResourceItemMapper extends Mapper<RoleResourceItem> {

    int deleteByResourceItemId(Long itemId);

    int deleteByRoleIdAndFunctionId(Long roleId, Long functionId);

    int insert(RoleResourceItem record);

    List<RoleResourceItem> selectResourceItemsByRole(Long roleId);

    RoleResourceItem selectByRoleIdAndResourceItemId(Long roleId, Long resourceItemId);

    List<RoleResourceItem> selectForCache();
}
