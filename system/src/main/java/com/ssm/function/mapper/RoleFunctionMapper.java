package com.ssm.function.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ssm.function.dto.RoleFunction;
import com.ssm.mybatis.common.Mapper;

public interface RoleFunctionMapper extends Mapper<RoleFunction>{
	
	List<RoleFunction> selectRoleFunctions(RoleFunction record);

    List<Map<String, Object>> selectAllRoleResources();

    int deleteByFunctionId(Long functionId);

    int deleteByRoleId(Long roleId);

    int selectCountByFunctionCode(@Param("roleId") Long roleId, @Param("functionCode") String functionCode);

}
