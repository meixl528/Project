package com.ssm.mail;

/**
 * 优先级枚举
 */
public enum PriorityLevelEnum {
    
    VIP("VIP"),
    NORMAL("NORMAL");
    
    private String code;
    
    private PriorityLevelEnum(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}