package com.ssm.mail;

/**
 * 消息类型枚举
 */
public enum MessageTypeEnum {

    EMAIL("EMAIL"), SMS("SMS"), DSIS("DSIS"), SITE("SITE");

    private String code;

    private MessageTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}