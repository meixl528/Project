package com.ssm.fnd.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ssm.attachment.dto.SysFile;
import com.ssm.core.request.IRequest;
import com.ssm.fnd.dto.FileMenuItem;
import com.ssm.message.profile.GlobalProfileListener;

public interface IFTPService extends GlobalProfileListener{

    SysFile upload(IRequest requestContext, String folder, CommonsMultipartFile file) throws Exception;
	
	boolean delete(String folder, String fileName) throws Exception;

	/**
	 * 文件下载
	 * @param filePath 文件所在路径
	 * @param fileName 文件名
	 * @param newName  文件别名
	 * @throws Exception
	 */
	void download(HttpServletRequest request, HttpServletResponse response, String filePath, String fileName,String newName) throws Exception;

	/**
	 * 获取服务器文件夹目录菜单
	 * @return
	 */
	List<FileMenuItem> getServerFolderMenu();

	
	
}
