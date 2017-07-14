package com.ssm.attachment.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import com.ssm.core.annotation.MultiLanguage;
import com.ssm.core.annotation.MultiLanguageField;
import com.ssm.sys.dto.BaseDTO;

/**
 * 文件分类DTO.
 */
@MultiLanguage
@Table(name = "tb_ATTACH_CATEGORY_B")
public class AttachCategory extends BaseDTO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 设置类型分隔符.
     */
    public static final String TYPE_SEPARATOR = ";";

    /**
     * 顶层分类，没有父id.
     */
    public static final Long NO_PARENT = -1L;

    /**
     * 正常.
     */
    public static final String STATUS_NORMAL = "1";
    /**
     * 归档.
     */
    public static final String STATUS_DELETED = "2";
    /**
     * 是叶子节点.
     */
    public static final String LEAF_TRUE = "1";
    /**
     * 不是叶子节点.
     */
    public static final String LEAF_FALSE = "0";
//    /**
//     * 是唯一.
//     */
//    public static final String ISUNIQUE_TRUE = "Y";
//    /**
//     * 不是唯一.
//     */
//    public static final String ISUNIQUE_FALSE = "N";

    /**
     * 默认是设置sourceType设置.
     */
    public static final String DEFAULT_SOURCETYPE = "folder";
    /**
     * 类型id.
     */
    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long categoryId;

    /**
     * 类型名字.
     */
    @Column
    @MultiLanguageField
    private String categoryName;

    /**
     * 类型路径.
     */
    @Column
    private String categoryPath;

    /**
     * 层级路径
     */
    private String path;


    /**
     * 可上传类型,以后缀名结尾.
     */
    @Column
    private String allowedFileType;

    /**
     * 可上传单个文件大小.
     */
    @Column
    private Long allowedFileSize;

    /**
     * 是否.
     */
    @Column
    private String leafFlag;

    /**
     * 分类描述.
     */
    @Column
    @MultiLanguageField
    private String description;

    @Column
    private String status;

    @Column
    private Long parentCategoryId;

    @Column
    private String sourceType;

    @Column
    private String isUnique;

    public String getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(String isUnique) {
        this.isUnique = isUnique;
    }

    public AttachCategory() {
        super();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLeafFlag() {
        return leafFlag;
    }

    public void setLeafFlag(String leafFlag) {
        this.leafFlag = leafFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getSourceType() {
        // 统一sourceType都是大写
        if (StringUtils.isNotBlank(sourceType)) {
            return sourceType.trim().toUpperCase();
        }
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getAllowedFileType() {
        return allowedFileType;
    }

    public void setAllowedFileType(String allowedFileType) {
        this.allowedFileType = allowedFileType;
    }

    public Long getAllowedFileSize() {
        return allowedFileSize;
    }

    public void setAllowedFileSize(Long allowedFileSize) {
        this.allowedFileSize = allowedFileSize;
    }

}
