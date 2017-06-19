package com.ssm.sys.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssm.sys.dto.CodeValue;

import com.ssm.mybatis.common.Mapper;

/**
 * @author meixl
 */
public interface CodeValueMapper extends Mapper<CodeValue> {
    int deleteByCodeId(CodeValue key);

    int deleteTlByCodeId(CodeValue key);

    List<CodeValue> selectCodeValuesByCodeName(String codeName);

    List<CodeValue> queryMsgTemCodeLov(@Param("value") String value, @Param("meaning") String meaning);
    
    List<CodeValue> queryEmlAccountCodeLov(@Param("value") String value, @Param("meaning") String meaning);

    List<CodeValue> selectCodeValuesByCodeId(CodeValue codeValue);
}