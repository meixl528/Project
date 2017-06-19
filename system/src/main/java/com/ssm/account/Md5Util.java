package com.ssm.account;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	
	private static final int MD5_TIMES = 100;
	//十六进制下数字到字符的映射数组   
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
	
    /**
     * MD5 加密的密码
     * @param password
     * @return
     */
    public static String MD5(String password){
		try {
			// 确定计算方法
	        MessageDigest md5 = MessageDigest.getInstance("MD5");
	        // 加密后的字符串
	        for (int i = 0; i < MD5_TIMES; i++) {
	        	 password = byteArrayToHexString(md5.digest(password.getBytes("utf-8")));
			}
	        return java.util.Base64.getEncoder().encodeToString(password.getBytes("utf-8"));
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 继续十六进制加密
	 * @return
	 */
	private static String byteArrayToHexString(byte[] b){   
       StringBuffer resultSb = new StringBuffer();   
       for (int i = 0; i < b.length; i++){   
            resultSb.append(byteToHexString(b[i]));   
       }   
       return resultSb.toString();   
    }   
     
	private static String byteToHexString(byte b){   
       int n = b;   
       if (n < 0)   
            n = 256 + n;   
       int d1 = n / 16;   
       int d2 = n % 16;   
       int d3 = d2==0?d1:d1/d2;
       return hexDigits[d1] + hexDigits[d2] + hexDigits[d3] + d3;   
    }

}
