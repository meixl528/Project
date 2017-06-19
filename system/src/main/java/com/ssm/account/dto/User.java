package com.ssm.account.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
	private Long id;
	
	@Column
	@Condition(operator=LIKE,autoWrap = true)
	private String name;
	
	@Column
	private String pass;
	
	@Column
	private String email;
	
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	//@JsonFormat(pattern = "yyyy-MM-dd")
	@Column
	private Date actvFrom;
	
	//@DateTimeFormat(pattern = "yyyy-MM-dd")
	//@JsonFormat(pattern = "yyyy-MM-dd")
	@Column
	private Date actvTo;
	
	@Column
	private String status;
	
	@Column
	private String telephone;
	
	@Column
	private String address;
	
	@Column
	private String description;
	
	public User(){}
	
	public User(String name,String pass){
		this.name = name;
		this.pass = pass;
	}

	public User(String name, String pass, String telephone, String address) {
		this.name = name;
		this.pass = pass;
		this.telephone = telephone;
		this.address = address;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
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

	public Date getActvFrom() {
		return actvFrom;
	}

	public void setActvFrom(Date actvFrom) {
		this.actvFrom = actvFrom;
	}
	public Date getActvTo() {
		return actvTo;
	}
	public void setActvTo(Date actvTo) {
		this.actvTo = actvTo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
