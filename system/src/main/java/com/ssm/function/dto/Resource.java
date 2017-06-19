/*
 * #{copyright}#
 */
package com.ssm.function.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.mybatis.annotation.Condition;
import com.ssm.sys.dto.BaseDTO;

/**
 * 资源DTO.
 * @author meixl
 */
//@MultiLanguage
@Table(name = "tb_resource_b")
public class Resource extends BaseDTO {

    private static final long serialVersionUID = 4235924417363951462L;

    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long resourceId;

    @NotEmpty
    private String accessCheck;

//    @MultiLanguageField
    @Column
    @Condition(operator = LIKE)
    private String description;

    @NotEmpty
    private String loginRequire;

//    @MultiLanguageField
    @Column
    @NotEmpty
    @Condition(operator = LIKE)
    private String name;

    @NotEmpty
    private String type;

    @NotEmpty
    @Condition(operator = LIKE)
    private String url;

    public String getAccessCheck() {
        return accessCheck;
    }

    public String getDescription() {
        return description;
    }

    public String getLoginRequire() {
        return loginRequire;
    }

    public String getName() {
        return name;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setAccessCheck(String accessCheck) {
        this.accessCheck = accessCheck;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setLoginRequire(String loginRequire) {
        this.loginRequire = loginRequire == null ? null : loginRequire.trim();
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }


}