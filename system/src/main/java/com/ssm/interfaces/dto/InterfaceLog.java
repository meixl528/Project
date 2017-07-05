package com.ssm.interfaces.dto;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssm.sys.dto.BaseDTO;

/**
 * @name        InterfaceLog
 * @description 接口记录表
 * @author      meixl
 * @date        2017年7月4日上午10:41:56
 * @version
 */
@Table(name = "tb_interface_log")
public class InterfaceLog extends BaseDTO {
	private static final long serialVersionUID = -1918894430440838285L;
	
	public InterfaceLog() {}

	/**
	 * @param interfaceUrl   接口url
	 * @param interfaceCode  接口代码(可为空)
	 * @param status         状态
	 */
	public InterfaceLog(String interfaceUrl, String interfaceCode, String status) {
		this(interfaceUrl,interfaceCode,status,null,null);
	}
	
	/**
	 * @param interfaceUrl   接口url
	 * @param interfaceCode  接口代码(可为空)
	 * @param status         状态
	 * @param coreData       核心数据
	 */
	public InterfaceLog(String interfaceUrl, String interfaceCode, String status,String coreData) {
		this(interfaceUrl,interfaceCode,status,coreData,null);
	}

	/**
	 * @param interfaceUrl   接口url
	 * @param interfaceCode  接口代码(可为空)
	 * @param status         状态
	 * @param coreData       核心数据
	 * @param logMessage     错误信息
	 */
	public InterfaceLog(String interfaceUrl, String interfaceCode, String status, String coreData, String logMessage) {
		this.interfaceUrl = interfaceUrl;
		this.interfaceCode = interfaceCode;
		this.status = status;
		this.coreData = coreData;
		this.logMessage = logMessage;
	}

	@Id
	@GeneratedValue
	private Long interfaceLogId;// 表ID，主键，供其他表做外键
	
	@NotEmpty
	private String interfaceUrl;

	@NotEmpty
	private String interfaceCode;// 接口代码，快码ITF.INTERFACE_WEBSERVICE

	@NotEmpty
	private String status;// 接口运行状态，S:成功，E:失败

	private String coreData;// 入站核心数据

	private String logMessage;// 日志消息
	
	@Transient
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date runDate;// 查询字段,运行时间(链接到lastUpdateDate,因为框架中把这个字段JsonIgnore了)
	
	@Transient
	private Date dateFrom;// 查询字段,记录日期从
	
	@Transient
	private Date dateTo;// 查询字段,记录日期从

	public void setInterfaceLogId(Long interfaceLogId) {
		this.interfaceLogId = interfaceLogId;
	}

	public Long getInterfaceLogId() {
		return interfaceLogId;
	}

	/**
	 * @return the interfaceCode
	 */
	public String getInterfaceCode() {
		return interfaceCode;
	}

	/**
	 * @param interfaceCode the interfaceCode to set
	 */
	public void setInterfaceCode(String interfaceCode) {
		this.interfaceCode = interfaceCode;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setCoreData(String coreData) {
		this.coreData = coreData;
	}

	public String getCoreData() {
		return coreData;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	public String getLogMessage() {
		return logMessage;
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * @param dateFrom the dateFrom to set
	 */
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	/**
	 * @return the dateTo
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * @param dateTo the dateTo to set
	 */
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	/**
	 * @return the runDate
	 */
	public Date getRunDate() {
		return runDate;
	}

	/**
	 * @param runDate the runDate to set
	 */
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

	public String getInterfaceUrl() {
		return interfaceUrl;
	}

	public void setInterfaceUrl(String interfaceUrl) {
		this.interfaceUrl = interfaceUrl;
	}
	
}
