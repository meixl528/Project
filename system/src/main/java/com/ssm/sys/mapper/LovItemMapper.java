package com.ssm.sys.mapper;

import java.util.List;

import com.ssm.mybatis.common.Mapper;
import com.ssm.sys.dto.LovItem;

/**
 * @author meixl
 */
public interface LovItemMapper extends Mapper<LovItem> {

    List<LovItem> selectByLovId(Long lovId);

    int deleteByLovId(Long lovId);
}
