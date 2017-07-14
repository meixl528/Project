package com.ssm.mail;

/**
 * 邮箱状态枚举
 */
public enum EmailStatusEnum {
    
    SUCCESS("SUCCESS"),
    ERROR("ERROR");
    
    private String code;
    
    private EmailStatusEnum(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}