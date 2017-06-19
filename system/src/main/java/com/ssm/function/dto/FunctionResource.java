/*
 * #{copyright}#
 */
package com.ssm.function.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ssm.sys.dto.BaseDTO;

/**
 * 功能资源DTO.
 * @author meixl
 */
@Table(name = "tb_function_resource")
public class FunctionResource extends BaseDTO {

    private static final long serialVersionUID = 2205839053452054599L;

    @Id
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long funcSrcId;

    private Long functionId;

    private Long resourceId;

    public Long getFuncSrcId() {
        return funcSrcId;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setFuncSrcId(Long funcSrcId) {
        this.funcSrcId = funcSrcId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
}