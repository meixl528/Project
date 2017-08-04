package com.ssm.fnd.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ssm.attachment.dto.SysFile;
import com.ssm.attachment.service.ISysFileService;
import com.ssm.core.request.IRequest;
import com.ssm.fnd.service.IFTPService;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.responceFactory.ResponseData;

@Controller
public class FTPController extends BaseController{
	
	Logger logger = LoggerFactory.getLogger(FTPController.class);
	
	@Autowired
	private IFTPService ftpService;
	
	@Autowired
    private ISysFileService sysFileService;
	
	@RequestMapping(value = "/fnd/ftp/query")
	public ResponseData query(SysFile sysFile, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request){
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(sysFileService.selectFiles(requestContext, sysFile, page, pageSize));
	}
	
	@RequestMapping(value = "/fnd/ftp/remove")
	public ResponseData query(HttpServletRequest request,@RequestBody List<SysFile> sysFiles){
		for (SysFile sysFile : sysFiles) {
			sysFileService.deletefile(sysFile);
			try {
				ftpService.delete(sysFile.getFilePath(), sysFile.getFileName());
			} catch (Exception e) {
				logger.error("delete "+sysFile.getFileName()+" failed from FTP");
			}
		}
		return new ResponseData();
	}
	
	
	@RequestMapping(value = "/fnd/ftp/upload")
	@ResponseBody
	public ResponseData upload(HttpServletRequest request,@RequestParam("file") CommonsMultipartFile[] files,@RequestParam("folder") String folder){
		IRequest requestContext = createRequestContext(request);
		
		ResponseData response = new ResponseData(true);
		List<SysFile> list = new ArrayList<>();
		try {
			for (CommonsMultipartFile file : files) {
				list.add(ftpService.upload(folder, file));
			}
			for (SysFile file : list) {
				sysFileService.insert(requestContext, file);
			}
		} catch (Exception e) {
			for (SysFile file : list) {
				String filePath = file.getFilePath();
				String folde = filePath.substring(0, filePath.lastIndexOf("/"));
				String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
				try {
					ftpService.delete(folde, fileName);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			response.setSuccess(false);
		}
		return response;
	}
	
	@RequestMapping(value = "/fnd/ftp/download")
	public void download(HttpServletRequest request,HttpServletResponse response,String fileName,String newName) {
		try {
			ftpService.download(request, response, fileName, newName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/fnd/ftp/delete")
	@ResponseBody
	public ResponseData delete(String folder,String fileName){
		ResponseData res = new ResponseData(true);
		try {
			ftpService.delete(folder, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			res.setSuccess(false);
		}
		return res;
	}
}
