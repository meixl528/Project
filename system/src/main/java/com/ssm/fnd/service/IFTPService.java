package com.ssm.fnd.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ssm.attachment.dto.SysFile;

public interface IFTPService {

    SysFile upload(String folder, CommonsMultipartFile file) throws Exception;
	
	boolean delete(String folder, String fileName) throws Exception;

	void download(HttpServletRequest request, HttpServletResponse response, String fileName, String newName)
			throws Exception;
	
}
