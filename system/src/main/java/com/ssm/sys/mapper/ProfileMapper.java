package com.ssm.sys.mapper;
import com.ssm.mybatis.common.Mapper;
import com.ssm.sys.dto.Profile;

/**
 */
public interface ProfileMapper extends Mapper<Profile> {

    Profile selectByName(String profileName);
}