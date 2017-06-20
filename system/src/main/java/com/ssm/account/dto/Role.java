package com.ssm.account.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ssm.core.annotation.MultiLanguage;
import com.ssm.mybatis.annotation.Condition;
import com.ssm.sys.dto.BaseDTO;

@MultiLanguage
@Table(name="tb_role_b")
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
	private String enableFlag;
	
	@Column
    @Condition(operator = ">=")
    private Date startActiveDate;

    @Column
    @Condition(operator = "<=")
    private Date endActiveDate;
	
	@Column
	private String roleDescription;
	
	@Transient
	private Long userId; 
	@Transient
	private String userName;
	
	public Role(){}
	
	public Role(String roleCode,String roleName,String roleDescription){
		this.roleCode = roleCode;
		this.roleName = roleName;
		this.roleDescription = roleDescription;
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

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
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

	public String getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(String enableFlag) {
		this.enableFlag = enableFlag;
	}

	public Date getStartActiveDate() {
		return startActiveDate;
	}

	public void setStartActiveDate(Date startActiveDate) {
		this.startActiveDate = startActiveDate;
	}

	public Date getEndActiveDate() {
		return endActiveDate;
	}

	public void setEndActiveDate(Date endActiveDate) {
		this.endActiveDate = endActiveDate;
	}
	
}
