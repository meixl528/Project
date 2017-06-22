package com.ssm.util;

import java.text.NumberFormat;
import java.util.Locale;

import com.ssm.core.request.IRequest;

/**
 * @name        FndUtil
 * @description 功能util类
 * @author      meixl
 * @date        2017年6月20日下午12:00:45
 */
public class FndUtil {
	
	/**
	 * 从IRequest对象中获得Locale对象
	 * @param request
	 * @return
	 */
	public static Locale getLocale(IRequest request){
		String[] localeInfo = request.getLocale().split("_");
		Locale locale = null;
		if(localeInfo.length == 1){
			locale = new Locale(localeInfo[0]);
		}else{
			locale = new Locale(localeInfo[0], localeInfo[1]);
		}
		return locale;
	}

	// 格式化流水号
	public static String numberFormat(Long num, int length) {
		NumberFormat nf = NumberFormat.getInstance();
		//设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMinimumIntegerDigits(length);
		return nf.format(num);
	}

}
