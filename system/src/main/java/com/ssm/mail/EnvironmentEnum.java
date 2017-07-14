package com.ssm.mail;

/**
 * 系统环境枚举
 */
public enum EnvironmentEnum {
    
    SIT("SIT"),
    UAT("UAT"),
    PROD("PROD");
    
    private String code;
    
    private EnvironmentEnum(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}