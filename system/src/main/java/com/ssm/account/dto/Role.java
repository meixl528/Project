package com.ssm.account.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ssm.sys.dto.BaseDTO;

@Table(name="tb_role")
public class Role extends BaseDTO{
	
	public static final String FIELD_ROLE_ID = "roleId";
	public static final String FIELD_ROLE_NAME = "roleName";
	
	private static final long serialVersionUID = 3323649742604434578L;
	@Id
	@GeneratedValue(generator = GENERATOR_TYPE)
	private Long roleId;
	
	@Column
	private String roleCode;
	
	@Column
	private String roleName;
	
	@Column
	private String shortName;
	
	@Column
	private String description;
	
	@Transient
	private Long userId; 
	@Transient
	private String userName;
	
	public Role(){}
	
	public Role(String roleCode,String roleName,String description){
		this.roleCode = roleCode;
		this.roleName = roleName;
		this.description = description;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
