package com.ssm.util;

public class StringUtil {
	
	public static boolean isNull(String str){
		return (str == "" || str == null || str.equals("null"))?true:false;
	}
	
	public static boolean isNotNull(String str){
		return !isNull(str);
	}
	
	/**
     * 空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

}
