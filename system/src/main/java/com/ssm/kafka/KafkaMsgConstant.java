package com.ssm.kafka;

/**
 * kafkaMessageConstant
 */
public class KafkaMsgConstant {

    public static final String SUCCESS_CODE = "S";
    public static final String SUCCESS_MES = "成功";
    
    
    public static final String KAFKA_SEND_ERROR_CODE = "E";
    public static final String KAFKA_SEND_ERROR_MES = "发送消息超时,联系相关技术人员";
    
    
    public static final String KAFKA_NO_RESULT_CODE = "N_R";
    public static final String KAFKA_NO_RESULT_MES = "未查询到返回结果,联系相关技术人员";
    
    
    public static final String KAFKA_NO_OFFSET_CODE = "N_O";
    public static final String KAFKA_NO_OFFSET_MES = "未查到返回数据的offset,联系相关技术人员";
    
}