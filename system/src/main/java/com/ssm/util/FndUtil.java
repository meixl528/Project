package com.ssm.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.ssm.core.request.IRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @name        FndUtil
 * @description 功能util类
 * @author      meixl
 * @date        2017年6月20日下午12:00:45
 */
public class FndUtil {
	
	/**
	 * 项目简码,原纸交易平台
	 */
	public static final String WG_PROJECT_CODE = "paper";
	/**
	 * OSS公共读 BUCKET
	 */
	public static final String WG_PUBLIC_BUCKET = WG_PROJECT_CODE + "-public";
	/**
	 * OSS私有 BUCKET
	 */
	public static final String WG_PRIVATE_BUCKET = WG_PROJECT_CODE + "-private";
	/**
	 * 允许跨域设置
	 */
	public static final String WG_ALLOW_ORIGIN = "*";
	/**
	 * Token标签名称
	 */
	public static final String WG_TOKEN_TAG = "__wgt";
	/**
	 * Lang语言标签名称
	 */
	public static final String WG_LANG_TAG = "__wgl";
	/**
	 * 提醒消息队列名称
	 */
	public static final String WG_ALERT_MESSAGE_MQ = "alert_message";
	
	/**
	 * 内容缓存Key超时天数
	 */
	public static final Long TIME_UNIT_DAYS = 30L;
	
	/**
	 * 分布式锁超时默认分钟数
	 */
	public static final Long TIME_UNIT_MINS = 5L;
	
	/**
	 * 格式化流水号
	 * 
	 * @param num
	 *            流水号
	 * @param length
	 *            长度
	 * @return 格式化后流水号
	 */
	public static String numberFormat(Long num, int length) {
		NumberFormat nf = NumberFormat.getInstance();
		// 设置是否使用分组
		nf.setGroupingUsed(false);
		// 设置最大整数位数
		nf.setMinimumIntegerDigits(length);
		return nf.format(num);
	}

	/**
	 * 获取当前时间时间戳字符串
	 * 
	 * @return 时间戳
	 */
	public static String getTimeStamp() {
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
	}

	/**
	 * 获取当前时间精确到时分秒
	 * 
	 * @return Date
	 */
	public static Date getCurrentDatetime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(new Date());
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
		}
		return null;
	}
	
	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 获取当前日期字符串
	 * 
	 * @return 年月日 YYYY-MM-DD
	 */
	public static String getCurrentDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
	
	/**
	 * 获取当前日期字符串
	 * 
	 * @return 年月日 YYYY-MM-DD HH:mm:ss
	 */
	public static String getCurrentDateTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	/**
	 * 根据日期及格式获取时间字符串
	 * 
	 * @param date
	 * @param dateFormat
	 * @return 时间
	 */
	public static String getDateByFormat(Date date, String dateFormat) {
		return new SimpleDateFormat(dateFormat).format(date);
	}

	/**
	 * 获取指定时间的Long
	 * 
	 * @return
	 */
	public static Long getTimeStamp(Date date) {
		return date.getTime();
	}
	
	/**
	 * 获取当前时间的Long
	 * 
	 * @return
	 */
	public static Long getCurrentTimeStamp() {
		return new Date().getTime();
	}

	/**
	 * 把对象转换成页面需要展示属性的字符串通用方法
	 * 
	 * @param o
	 * @param args
	 * @return
	 */
	public static String toJsonString(Object o, String... args) {
		Class<? extends Object> clazz = null;
		JSONObject jobj = new JSONObject();
		try {
			clazz = o.getClass();
			for (String s : args) {
				// 如果是带默认值形式，则使用默认值
				if (s.contains(":")) {
					String[] partS = s.split(":");
					jobj.element(partS[0], partS[1]);
				} else {
					PropertyDescriptor pd = new PropertyDescriptor(s, clazz);
					Method getMethod = pd.getReadMethod();// 获得get方法
					Object value = getMethod.invoke(o);// 执行get方法返回一个Object

					if (value != null) {
						// 判断是是list的子类型
						if (value instanceof List) {
							jobj.element(s, JSONArray.fromObject(value).toString());
						} else {
							jobj.element(s, value.toString());
						}
					} else {
						jobj.element(s, "null");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "[]";
		}
		return jobj.toString();
	}

	/**
	 * 字符串进行MD5加密处理
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 获取Double格式化数据
	 * @param d 被格式化数据
	 * @param digit 保留小数位
	 * @return
	 */
	public static Double getDoubleFormat(Double d,int digit){
		BigDecimal b = new BigDecimal(d); 
		return b.setScale(digit,BigDecimal.ROUND_HALF_UP).doubleValue();  
	}
	
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

	/**
	 * 根据语言字符串获得Locale对象
	 * @param localeStr zh_CN / en_GB
	 */
	public static Locale getLocale(String localeStr){
		String[] localeInfo = localeStr.split("_");
		Locale locale = null;
		if(localeInfo.length == 1){
			locale = new Locale(localeInfo[0]);
		}else{
			locale = new Locale(localeInfo[0], localeInfo[1]);
		}
		return locale;
	}
}
