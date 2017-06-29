package com.ssm.fnd.dto;

/**Auto Generated By Hap Code Generator**/
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.mybatis.annotation.Condition;
import com.ssm.sys.dto.BaseDTO;

//@ExtensionAttribute(disable = true)
@Table(name = "tb_fnd_style_template")
public class StyleTemplate extends BaseDTO {
	private static final long serialVersionUID = 6893816863665104145L;

	@Id
	@GeneratedValue
	private Long templateId;// 表ID，主键，供其他表做外键

	@NotEmpty
	@Condition(operator = "LIKE",autoWrap = false)
	private String templateCode;// 模板代码

	@NotEmpty
	@Condition(operator = "LIKE",autoWrap = false)
	private String templateName;// 模板名称;

	private String templateContent;// 模版内容

	private String isHorizontal;// 是否横向

	private String logFlag;// 日志存储标识
	
	@NotEmpty
	private String enabledFlag;// 生效标识：Y:生效，N:失效

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public String getIsHorizontal() {
		return isHorizontal;
	}

	public void setIsHorizontal(String isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	/**
	 * @return the logFlag
	 */
	public String getLogFlag() {
		return logFlag;
	}

	/**
	 * @param logFlag the logFlag to set
	 */
	public void setLogFlag(String logFlag) {
		this.logFlag = logFlag;
	}

	public void setEnabledFlag(String enabledFlag) {
		this.enabledFlag = enabledFlag;
	}

	public String getEnabledFlag() {
		return enabledFlag;
	}
}