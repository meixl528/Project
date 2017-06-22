package com.ssm.sys.mapper;

import java.util.List;

import com.ssm.mybatis.common.Mapper;
import com.ssm.sys.dto.Lov;

/**
 * @author meixl
 */
public interface LovMapper extends Mapper<Lov> {

    Lov selectByCode(String code);
    
    List<Lov> selectLovs(Lov lov);

}
