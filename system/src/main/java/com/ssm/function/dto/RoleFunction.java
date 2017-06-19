/*
 * #{copyright}#
 */
package com.ssm.function.dto;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssm.sys.dto.BaseDTO;

/**
 * 角色功能DTO.
 * @author meixl
 */
@Table(name = "tb_role_function")
public class RoleFunction extends BaseDTO {

    private static final long serialVersionUID = 688371423171208115L;

    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long srfId;

    private Long functionId;

    private Long roleId;

    public Long getFunctionId() {
        return functionId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getSrfId() {
        return srfId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setSrfId(Long srfId) {
        this.srfId = srfId;
    }

}