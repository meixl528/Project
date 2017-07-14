package com.ssm.mail;

/**
 * 邮件接收人类型枚举
 */
public enum ReceiverTypeEnum {
    
    NORMAL("NORMAL"),//普通收件人
    CC("CC"),//抄送
    BCC("BCC");//密送
    
    private String code;
    
    private ReceiverTypeEnum(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }
}