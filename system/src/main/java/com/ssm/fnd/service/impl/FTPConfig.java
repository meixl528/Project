package com.ssm.fnd.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ssm.message.profile.GlobalProfileListener;

/**
 * @name        FTPConfig
 * @description FTP服务器配置
 * @author      meixl
 * @date        2017年8月9日下午2:06:26
 * @version
 */
@Component
public class FTPConfig implements GlobalProfileListener{
	
	protected static String ftp_ip;
	protected static int ftp_port;
	protected static String ftp_name;
	protected static String ftp_pass;
	protected static String ftp_basepath;
	protected static boolean ftp_datepath;
	protected static boolean ftp_numforder;
	

	@Override
	public List<String> getAcceptedProfiles() {
		// FTP服务器配置(FTP_IP,FTP_PORT,FTP_NAME,FTP_PASS,FTP_BASEPATH,FTP_DATEPATH日期格式文件夹,FTP_NUMFOLDER显示数字格式文件夹)
		return Arrays.asList("FTP_SERVER_PROFILE");
	}

	@Override
	public void updateProfile(String profileName, String profileValue) {
		String[] config = profileValue.split(",");
		if (config.length >= 7) {
			ftp_ip = config[0];                              // ip 地址
			ftp_port = Integer.valueOf(config[1]);           // port 端口
			ftp_name = config[2];                            // ftp_name 用户名
			ftp_pass = config[3];                            // ftp_pass 用户密码
			ftp_basepath = config[4];                        // ftp 服务器根目录
			ftp_datepath = Boolean.getBoolean(config[5]);    // 使用日期格式目录  /2017/08/08
			ftp_numforder = Boolean.valueOf(config[6]);      // 显示数字/日期格式目录
		}
	}

}
