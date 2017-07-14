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
    private Long urId;

    @Column
    private Long userId;

    @Column
    private Long roleId;

    @Column
    private String defaultFlag;
    
    @Column
    private String enableFlag;

	public Long getUrId() {
		return urId;
	}

	public void setUrId(Long urId) {
		this.urId = urId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(String defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public String getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(String enableFlag) {
		this.enableFlag = enableFlag;
	}
	
}
