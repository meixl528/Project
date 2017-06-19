package com.ssm.sys.dto;

import com.ssm.mybatis.annotation.Condition;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 语言DTO.
 * @author meixl
 */
@Table(name = "tb_lang_b")
public class Language extends BaseDTO {

    private static final long serialVersionUID = -2978619661646886631L;

    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private String langCode;

    @Condition(exclude = true)
    private String baseLang;

    @Condition(operator = LIKE)
    private String description;
    
    @Transient
    private String value;

    public String getBaseLang() {
        return baseLang;
    }

    public String getDescription() {
        return description;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setBaseLang(String baseLang) {
        this.baseLang = baseLang == null ? null : baseLang.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode == null ? null : langCode.trim();
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}