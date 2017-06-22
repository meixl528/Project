package com.ssm.sys.dto;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.core.BaseConstants;
import com.ssm.core.annotation.Children;
import com.ssm.mybatis.annotation.Condition;

/**
 * LovDTO.
 * @author meixl
 *
 *         2016年2月1日
 */
@Table(name = "tb_lov")
public class Lov extends BaseDTO {
    private static final long serialVersionUID = -466598144320311424L;

    @NotEmpty
    private String code;

    @Condition(operator = BaseConstants.LIKE)
    private String description;

    @Condition(exclude = true)
    private Integer height;

    @Id
    @GeneratedValue(generator = BaseConstants.GENERATOR_TYPE)
    private Long lovId;

    @Children
    @Transient
    private List<LovItem> lovItems;

    @Condition(exclude = true)
    private String placeholder;

    @Column
    private String sqlId;

    @Column
    private String customSql;

    @Column
    private Integer queryColumns;
    
    @Column
    private String customUrl;

    @NotEmpty
    private String textField;

    @Condition(operator = BaseConstants.LIKE)
    private String title;

    @NotEmpty
    @Condition(exclude = true)
    private String valueField;

    @Condition(exclude = true)
    private Integer width;

    @Condition(exclude = true)
    private String delayLoad = BaseConstants.NO;

    @Condition(exclude = true)
    private String needQueryParam = BaseConstants.NO;

    @Condition(exclude = true)
    @Column(name = "EDITABLE")
    private String editableFlag = BaseConstants.NO;

    @Condition(exclude = true)
    private String canPopup = BaseConstants.YES;

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getCustomSql() {
        return customSql;
    }

    public Integer getQueryColumns() {
        return queryColumns;
    }

    public void setQueryColumns(Integer queryColumns) {
        this.queryColumns = queryColumns;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    public String getNeedQueryParam() {
        return needQueryParam;
    }

    public void setNeedQueryParam(String needQueryParam) {
        this.needQueryParam = needQueryParam;
    }

    public String getDelayLoad() {
        return delayLoad;
    }

    public void setDelayLoad(String delayLoad) {
        this.delayLoad = delayLoad;
    }

    public String getEditableFlag() {
        return editableFlag;
    }

    public void setEditableFlag(String editable) {
        this.editableFlag = editable;
    }

    public String getCanPopup() {
        return canPopup;
    }

    public void setCanPopup(String canPopup) {
        this.canPopup = canPopup;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getHeight() {
        return height;
    }

    public Long getLovId() {
        return lovId;
    }

    public List<LovItem> getLovItems() {
        return lovItems;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getSqlId() {
        return sqlId;
    }

    public String getTextField() {
        return textField;
    }

    public String getTitle() {
        return title;
    }

    public String getValueField() {
        return valueField;
    }

    public Integer getWidth() {
        return width;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setLovId(Long lovId) {
        this.lovId = lovId;
    }

    public void setLovItems(List<LovItem> lovItems) {
        this.lovItems = lovItems;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId == null ? null : sqlId.trim();
    }

    public void setTextField(String textField) {
        this.textField = textField == null ? null : textField.trim();
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public void setValueField(String valueField) {
        this.valueField = valueField == null ? null : valueField.trim();
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

}
