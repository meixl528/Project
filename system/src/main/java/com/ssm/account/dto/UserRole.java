package com.ssm.account.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ssm.sys.dto.BaseDTO;

@Table(name="tb_user_role")
public class UserRole extends BaseDTO{

	/**
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = GENERATOR_TYPE)
	private Long id;
	
	private Long userId;
	
	private Long roleId;
	
	@Transient
	private String userName;
	@Transient
	private String roleCode;
	@Transient
	private String roleName;
	@Transient
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
