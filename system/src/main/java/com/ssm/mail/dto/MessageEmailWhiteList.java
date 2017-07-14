package com.ssm.mail.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssm.sys.dto.BaseDTO;

/**
 * 邮件白名单
 */
@Table(name = "Tb_MESSAGE_EMAIL_WHITE_LT")
public class MessageEmailWhiteList extends BaseDTO {
    
    private static final long serialVersionUID = 3293370176048833707L;

    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long id;

    private String description;
    
    private String address;

    private Long configId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

}