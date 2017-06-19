package com.ssm.account.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ssm.mybatis.annotation.Condition;
import com.ssm.sys.dto.BaseDTO;

@Table(name="tb_user")
public class User extends BaseDTO{
	
	public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_USER_NAME = "userName";
    public static final String FIELD_FULL_NAME = "fullName";
    
    public static final String FIELD_SESSION_USER = "session_user";
    
    public static final String STATUS_ACTV = "ACTV";
    public static final String STATUS_EXPR = "EXPR";
    public static final String STATUS_LOCK = "LOCK";
	
	private static final long serialVersionUID = 3323649742604434578L;
	@Id
	@Column
	@GeneratedValue(generator = GENERATOR_TYPE)
	private Long userId;
	
	@Column
	@Condition(operator=LIKE,autoWrap = true)
	private String userName;
	
	
	@Column
	private String password;
	
	@Column
	private String email;
	
	@Column
	private String phone;
	
	@Column
	private String address;
	
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
		//@JsonFormat(pattern = "yyyy-MM-dd")
	@Column
    @Condition(operator = ">=")
    private Date startActiveDate;

    @Column
    @Condition(operator = "<=")
    private Date endActiveDate;

    // 状态
    @JsonInclude(Include.NON_NULL)
    @Column
    private String status;

    // 最后一次登录时间
    @Column
    private Date lastLoginDate;

    // 最后一次修改密码时间
    @Column
    private Date lastPasswordUpdateDate;

    // 是否第一次登录
    @Column
    private String firstLogin;

    private String description;
    //员工ID
    private Long employeeId;
    //客户ID
    private Long customerId;
    //供应商ID
    private Long supplierId;
    @Transient
    private String employeeCode;
    @Transient
    private String employeeName;
	
	public User(){}
	
	public User(String userName,String password){
		this.userName = userName;
		this.password = password;
	}

	public User(String userName, String password, String phone, String address) {
		this.userName = userName;
		this.password = password;
		this.phone = phone;
		this.address = address;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Date getLastPasswordUpdateDate() {
		return lastPasswordUpdateDate;
	}

	public void setLastPasswordUpdateDate(Date lastPasswordUpdateDate) {
		this.lastPasswordUpdateDate = lastPasswordUpdateDate;
	}

	public String getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(String firstLogin) {
		this.firstLogin = firstLogin;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
