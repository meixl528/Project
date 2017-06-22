package com.ssm.sys.mapper;

import java.util.List;
import java.util.Map;

import com.ssm.sys.dto.MultiLanguageField;

/**
 * @author meixl
 */
public interface MultiLanguageMapper {

    List<MultiLanguageField> select(Map<String, String> map);
}