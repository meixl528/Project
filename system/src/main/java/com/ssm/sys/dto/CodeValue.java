package com.ssm.sys.dto;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.core.annotation.MultiLanguage;
import com.ssm.core.annotation.MultiLanguageField;

/**
 * CodeValueDTO.
 * @author meixl
 */
@MultiLanguage
@Table(name = "tb_code_value_b")
public class CodeValue extends BaseDTO {

    private static final long serialVersionUID = 7078027762943933806L;

    /**
     * 快速编码类型.
     */
    private Long codeId;

    /**
     * ID.
     */
    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long codeValueId;

    /**
     * 快码编码值描述.
     */
    @MultiLanguageField
    private String description;

    /**
     * 快码编码意义描述.
     */
    @MultiLanguageField
    @NotEmpty
    private String meaning;

    /**
     * 快速编码code.
     */
    @NotEmpty
    private String value;

    private Long orderSeq;

    private String  tag;

    private String enabledFlag;

    public Long getCodeId() {
        return codeId;
    }

    public Long getCodeValueId() {
        return codeValueId;
    }

    public String getDescription() {
        return description;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getValue() {
        return value;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public void setCodeValueId(Long codeValueId) {
        this.codeValueId = codeValueId;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning == null ? null : meaning.trim();
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }

    public String getTag() {return tag;}

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}