package com.ssm.sys.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssm.mybatis.annotation.ExtensionAttribute;

@Table(name = "tb_config")
@ExtensionAttribute(disable=true)
public class SysConfig extends BaseDTO {

	private static final long serialVersionUID = 1L;
	
	/**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @GeneratedValue
    private Long configId;

    /**
     * 配置名称.
     */
    private String configCode;

    /**
     * 配置值.
     */
    private String configValue;

    /**
     * 分类.
     */
    private String category;

	public Long getConfigId() {
		return configId;
	}

	public void setConfigId(Long configId) {
		this.configId = configId;
	}

	public String getConfigCode() {
		return configCode;
	}

	public void setConfigCode(String configCode) {
	   this.configCode = configCode == null ? null : configCode.trim();

	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue == null ? null : configValue.trim();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category == null ? null : category.trim();
	}



}