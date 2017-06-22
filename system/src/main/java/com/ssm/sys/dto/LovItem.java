package com.ssm.sys.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssm.core.BaseConstants;
import com.ssm.mybatis.annotation.Condition;
/**
 * LovItem.
 * @author meixl
 */
@Table(name = "tb_lov_item")
public class LovItem extends BaseDTO {

    private static final long serialVersionUID = -1573793167997659244L;

    @Id
    @GeneratedValue(generator = BaseConstants.GENERATOR_TYPE)
    private Long lovItemId;

    private Long lovId;

    private String display;

    @Condition(exclude = true)
    private String gridFieldName;

    @Condition(exclude = true)
    private Integer gridFieldWidth;

    @Condition(exclude = true)
    private String gridFieldAlign;

    @Condition(exclude = true)
    private String autocompleteField = BaseConstants.YES;

    @Condition(exclude = true)
    private String conditionField = BaseConstants.NO;

    @Condition(exclude = true)
    private String isAutocomplete = BaseConstants.NO;

    @Condition(exclude = true)
    private String gridField = BaseConstants.YES;

    @Condition(exclude = true)
    private Integer conditionFieldWidth;

    @Condition(exclude = true)
    private Integer conditionFieldLabelWidth;

    @Condition(exclude = true)
    private String conditionFieldType;

    @Condition(exclude = true)
    private String conditionFieldName;

    @Condition(exclude = true)
    private String conditionFieldTextfield;

    @Condition(exclude = true)
    private String conditionFieldNewline = BaseConstants.NO;

    @Condition(exclude = true)
    private String conditionFieldSelectUrl;

    @Condition(exclude = true)
    private String conditionFieldSelectVf;

    @Condition(exclude = true)
    private String conditionFieldSelectTf;

    @Condition(exclude = true)
    private String conditionFieldSelectCode;

    @Condition(exclude = true)
    private String conditionFieldLovCode;

    @Condition(exclude = true)
    private Integer conditionFieldSequence = 1;

    @Condition(exclude = true)
    private Integer gridFieldSequence = 1;

    public String getConditionFieldTextfield() {
        return conditionFieldTextfield;
    }

    public Integer getConditionFieldLabelWidth() {
        return conditionFieldLabelWidth;
    }

    public void setConditionFieldLabelWidth(Integer conditionFieldLabelWidth) {
        this.conditionFieldLabelWidth = conditionFieldLabelWidth;
    }

    public void setConditionFieldTextfield(String conditionFieldTextfield) {
        this.conditionFieldTextfield = conditionFieldTextfield;
    }

    /**
     * @return the conditionFieldSelectUrl
     */
    public String getConditionFieldSelectUrl() {
        return conditionFieldSelectUrl;
    }

    /**
     * @param conditionFieldSelectUrl
     *            the conditionFieldSelectUrl to set
     */
    public void setConditionFieldSelectUrl(String conditionFieldSelectUrl) {
        this.conditionFieldSelectUrl = conditionFieldSelectUrl;
    }

    /**
     * @return the conditionFieldSelectVf
     */
    public String getConditionFieldSelectVf() {
        return conditionFieldSelectVf;
    }

    /**
     * @param conditionFieldSelectVf
     *            the conditionFieldSelectVf to set
     */
    public void setConditionFieldSelectVf(String conditionFieldSelectVf) {
        this.conditionFieldSelectVf = conditionFieldSelectVf;
    }

    /**
     * @return the conditionFieldSelectTf
     */
    public String getConditionFieldSelectTf() {
        return conditionFieldSelectTf;
    }

    /**
     * @param conditionFieldSelectTf
     *            the conditionFieldSelectTf to set
     */
    public void setConditionFieldSelectTf(String conditionFieldSelectTf) {
        this.conditionFieldSelectTf = conditionFieldSelectTf;
    }

    /**
     * @return the autocompleteField
     */
    public String getAutocompleteField() {
        return autocompleteField;
    }

    /**
     * @param autocompleteField
     *            the autocompleteField to set
     */
    public void setAutocompleteField(String autocompleteField) {
        this.autocompleteField = autocompleteField;
    }

    public String getGridFieldAlign() {
        return gridFieldAlign;
    }

    public void setGridFieldAlign(String gridFieldAlign) {
        this.gridFieldAlign = gridFieldAlign != null ? gridFieldAlign.toLowerCase() : "center";
    }

    public Integer getConditionFieldSequence() {
        return conditionFieldSequence;
    }

    public void setConditionFieldSequence(Integer conditionFieldSequence) {
        this.conditionFieldSequence = conditionFieldSequence;
    }

    public Integer getGridFieldSequence() {
        return gridFieldSequence;
    }

    public void setGridFieldSequence(Integer gridFieldSequence) {
        this.gridFieldSequence = gridFieldSequence;
    }

    public String getConditionFieldType() {
        return conditionFieldType;
    }

    public void setConditionFieldType(String conditionFieldType) {
        this.conditionFieldType = conditionFieldType;
    }

    public String getConditionFieldName() {
        return conditionFieldName;
    }

    public void setConditionFieldName(String conditionFieldName) {
        this.conditionFieldName = conditionFieldName;
    }

    public String getConditionFieldNewline() {
        return conditionFieldNewline;
    }

    public void setConditionFieldNewline(String conditionFieldNewline) {
        this.conditionFieldNewline = conditionFieldNewline;
    }

    public String getConditionFieldSelectCode() {
        return conditionFieldSelectCode;
    }

    public void setConditionFieldSelectCode(String conditionFieldSelectCode) {
        this.conditionFieldSelectCode = conditionFieldSelectCode;
    }

    public String getConditionFieldLovCode() {
        return conditionFieldLovCode;
    }

    public void setConditionFieldLovCode(String conditionFieldLovCode) {
        this.conditionFieldLovCode = conditionFieldLovCode;
    }

    public Integer getConditionFieldWidth() {
        return conditionFieldWidth;
    }

    public void setConditionFieldWidth(Integer conditionFieldWidth) {
        this.conditionFieldWidth = conditionFieldWidth;
    }

    public String getGridField() {
        return gridField;
    }

    public void setGridField(String gridField) {
        this.gridField = gridField;
    }

    public Long getLovItemId() {
        return lovItemId;
    }

    public void setLovItemId(Long lovItemId) {
        this.lovItemId = lovItemId;
    }

    public Long getLovId() {
        return lovId;
    }

    public void setLovId(Long lovId) {
        this.lovId = lovId;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display == null ? null : display.trim();
    }

    public String getGridFieldName() {
        return gridFieldName;
    }

    public void setGridFieldName(String name) {
        this.gridFieldName = name == null ? null : name.trim();
    }

    public Integer getGridFieldWidth() {
        return gridFieldWidth;
    }

    public void setGridFieldWidth(Integer width) {
        this.gridFieldWidth = width;
    }

    public String getConditionField() {
        return conditionField;
    }

    public void setConditionField(String conditionField) {
        this.conditionField = conditionField == null ? null : conditionField.trim();
    }

    public void setIsAutocomplete(String isAutoComplete) {
        this.isAutocomplete = isAutoComplete;
    }

    public String getIsAutocomplete() {
        return isAutocomplete;
    }
}
