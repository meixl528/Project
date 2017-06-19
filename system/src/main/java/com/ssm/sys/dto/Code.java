package com.ssm.sys.dto;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.core.annotation.Children;
import com.ssm.core.annotation.MultiLanguage;
import com.ssm.core.annotation.MultiLanguageField;
import com.ssm.mybatis.annotation.Condition;

/**
 * CodeDTO.
 * @author meixl
 */
@MultiLanguage
@Table(name = "tb_code_b")
public class Code extends BaseDTO {
    
    private static final long serialVersionUID = 2776430709705510697L;

    /**
     * 快码类型.
     */
    @Condition(operator = LIKE)
    @Column
    @NotNull
    private String code;

    /**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long codeId;

    @Children
    @Transient
    private List<CodeValue> codeValues;

    /**
     * 快码描述.
     */
    @Column
    @MultiLanguageField
    @NotEmpty
    @Condition(operator = LIKE)
    private String description;


    private String type;

    private String enabledFlag;

    public String getCode() {
        return code;
    }

    public Long getCodeId() {
        return codeId;
    }

    public List<CodeValue> getCodeValues() {
        return codeValues;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public void setCodeValues(List<CodeValue> codeValues) {
        this.codeValues = codeValues;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}