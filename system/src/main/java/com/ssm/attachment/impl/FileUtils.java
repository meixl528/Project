package com.ssm.attachment.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件上传工具类.
 */
public class FileUtils {

    protected FileUtils() {

    }

    /**
     * yyyy/MM/dd.
     * 
     * @param date
     *            Date参数
     * @return string yyyy/MM/dd格式字符串
     */
    public static String formatYMD(Date date) {
        SimpleDateFormat YMD = new SimpleDateFormat("yyyy/MM/dd");
        return YMD.format(date);
    }

}
