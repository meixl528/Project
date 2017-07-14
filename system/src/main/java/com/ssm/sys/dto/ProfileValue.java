package com.ssm.sys.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Table(name = "tb_profile_value")
public class ProfileValue extends BaseDTO {

    private static final long serialVersionUID = -6967222521922622857L;

    /**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long profileValueId;

    /**
     * 配置文件ID.
     */
    @NotNull
    private Long profileId;

    /**
     * 层次ID值.
     */
    @NotNull
    private Long levelId;

    /**
     * 层次值.
     */
    @NotEmpty
    private String levelValue;

    @Transient
    private String levelName;

    /**
     * 配置文件值.
     */
    private String profileValue;

    public Long getProfileValueId() {
        return profileValueId;
    }

    public void setProfileValueId(Long profileValueId) {
        this.profileValueId = profileValueId;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue == null ? null : levelValue.trim();
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName == null ? null : levelName.trim();
    }

    public String getProfileValue() {
        return profileValue;
    }

    public void setProfileValue(String profileValue) {
        this.profileValue = profileValue == null ? null : profileValue.trim();
    }

}