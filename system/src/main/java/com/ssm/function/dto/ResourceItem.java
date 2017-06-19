package com.ssm.function.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.core.annotation.MultiLanguage;
import com.ssm.core.annotation.MultiLanguageField;
import com.ssm.sys.dto.BaseDTO;

/**
 * 资源权限项.
 * @author meixl
 */
@MultiLanguage
@Table(name = "tb_resource_item_b")
public class ResourceItem extends BaseDTO {
    
    private static final long serialVersionUID = -2814559439360957338L;

    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long resourceItemId;

    
    private Long ownerResourceId;

    private Long targetResourceId;

    //仅用于显示
    @Transient
    private String targetResourceName;
    

    @NotEmpty
    private String itemId;

    @MultiLanguageField
    @Column
    private String itemName;
    
    @MultiLanguageField
    @Column
    private String description;

    private String itemType;

	public Long getResourceItemId() {
		return resourceItemId;
	}

	public void setResourceItemId(Long resourceItemId) {
		this.resourceItemId = resourceItemId;
	}

	public Long getOwnerResourceId() {
		return ownerResourceId;
	}

	public void setOwnerResourceId(Long ownerResourceId) {
		this.ownerResourceId = ownerResourceId;
	}

	public Long getTargetResourceId() {
		return targetResourceId;
	}

	public void setTargetResourceId(Long targetResourceId) {
		this.targetResourceId = targetResourceId;
	}

	public String getTargetResourceName() {
		return targetResourceName;
	}

	public void setTargetResourceName(String targetResourceName) {
		this.targetResourceName = targetResourceName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
    
}
