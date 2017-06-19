/*
 * #{copyright}#
 */
package com.ssm.sys.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.mybatis.annotation.Condition;

/**
 * 多语言描述表.
 * @author meixl
 */
@Table(name = "tb_prompts")
public class Prompt extends BaseDTO {

    private static final long serialVersionUID = 2856108923186548186L;

    /**
     * 表ID，主键，供其他表做外键.
     */
    @Id
    //@GeneratedValue(generator = GENERATOR_TYPE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promptId;

    /**
     * 文本编码.
     */

    @NotEmpty
    @Condition(operator = LIKE)
    private String promptCode;

    /**
     * 语言.
     */
    private String lang;

    /**
     * 描述.
     */
    @Condition(operator = LIKE)
    private String description;

    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    public String getPromptCode() {
        return promptCode;
    }

    public void setPromptCode(String promptCode) {
        this.promptCode = promptCode == null ? null : promptCode.trim();
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang == null ? null : lang.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

}