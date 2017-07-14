package com.ssm.sys.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ssm.mybatis.common.Mapper;
import com.ssm.sys.dto.ProfileValue;

public interface ProfileValueMapper extends Mapper<ProfileValue> {

    int deleteByProfileId(ProfileValue key);

    List<ProfileValue> selectLevelValuesForUser(ProfileValue value);

    List<ProfileValue> selectLevelValuesForRole(ProfileValue value);

    List<ProfileValue> selectProfileValues(ProfileValue example);


    /**
     * 根据 profile id 和 user id 查询 该user id 在该配置文件下的所有值， 并按照 LEVEL_ID降序排列
     * 
     * @param profileId
     *            配置文件ID
     * @param userId
     *            用户ID
     * @return 配置文件值List
     */
    List<ProfileValue> selectByProfileIdAndUserId(@Param("profileId") Long profileId, @Param("userId") Long userId);

    /**
     * 根据request和profileName按优先级获取配置文件值.
     * 
     * @param profileName
     *            配置文件
     * @return 按优先级获取配置文件值List
     */
    List<ProfileValue> selectPriorityValues(String profileName);
}