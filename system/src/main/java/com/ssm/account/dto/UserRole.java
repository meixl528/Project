package com.ssm.account.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssm.sys.dto.BaseDTO;

@Table(name="tb_user_role")
public class UserRole extends BaseDTO {

    private static final long serialVersionUID = 2098581833914123800L;

    /**
     * 表ID，主键，供其他表做外键.
     *
     */
    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long surId;

    @Column
    private Long userId;

    @Column
    private Long roleId;

    @Column
    private String defaultFlag;

    public Long getRoleId() {
        return roleId;
    }

    public Long getSurId() {
        return surId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setSurId(Long surId) {
        this.surId = surId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
	
}
