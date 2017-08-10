package com.ssm.fnd.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ssm.attachment.dto.SysFile;
import com.ssm.core.request.IRequest;
import com.ssm.fnd.dto.FileMenuItem;
import com.ssm.fnd.service.IFTPService;
import com.ssm.fnd.service.ISequenceService;
/**
 * @name        FTPServcieImpl
 * @description ftp上传下载
 * @author      meixl
 * @date        2017年8月4日上午9:55:00
 * @version
 */
@Service
public class FTPServcieImpl extends FTPConfig implements IFTPService {
	/*@Value("${FTP_IP}")
	private String ftp_ip;
	@Value("${FTP_PORT}")
	private int ftp_port;
	@Value("${FTP_NAME}")
	private String ftp_name;
	@Value("${FTP_PASS}")
	private String ftp_pass;
	@Value("${FTP_BASEPATH}")
	private String ftp_basepath;
	@Value("${FTP_DATEPATH}")
	private boolean ftp_datepath;
	@Value("${FTP_NUMFOLDER}")
	private boolean ftp_numforder;*/
	
	@Autowired
	private ISequenceService iSequenceService;

	public FTPClient connect() throws Exception {
		// 创建一个FtpClient对象
		FTPClient ftpClient = new FTPClient();
		// 创建ftp连接,默认21端口
		ftpClient.connect(ftp_ip, ftp_port);
		// 登陆ftp服务器,使用用户名和密码
		boolean bool = ftpClient.login(ftp_name, ftp_pass);
		if (!bool)
			throw new Exception("ftp连接失败");
		return ftpClient;
	}

	/**
	 * ftp上传
	 *@param folder 文件上传到具体服务器上的目录下   如:invoice/excel
	 *@param file 上传的文件
	 */
	@Override
	public SysFile upload(IRequest iRequest,String folder, CommonsMultipartFile file) throws Exception {
		String datePath = "";
		if(ftp_datepath){
			datePath = new DateTime().toString("/yyyy/MM/dd");
		}
		// 创建一个FtpClient对象
		FTPClient ftpClient = connect();
		InputStream fileInputStream = file.getInputStream();
		// 设置上传路径
		if(!folder.startsWith("/")) folder = "/"+folder;
		if(folder.endsWith("/")) folder = folder.substring(0, folder.length()-1);
		if(!ftpClient.changeWorkingDirectory(ftp_basepath+folder+datePath)){
			String[] forlderArr = (folder+datePath).split("/");
			String tempPath = ftp_basepath;
			for(String fold :forlderArr){
				if(null==fold ||"".equals(fold)) continue;
				tempPath += "/" + fold;
				if (!ftpClient.changeWorkingDirectory(tempPath)) {
					if (!ftpClient.makeDirectory(tempPath)) {
						throw new Exception("创建文件夹  : "+tempPath +"失败");
					} else {
						ftpClient.changeWorkingDirectory(tempPath);
					}
				}
			}
		}
		// 修改上传文件格式 (二进制格式)
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		//本地 gbk编码
		String UUIDName = uuidName(file.getSize(),file.getOriginalFilename());
		// 服务器编码iso-8859-1 , 需要转码
		String serverUUIDName = new String(UUIDName.getBytes(), "iso-8859-1");
		// 参数1:服务器端文档名 , 参数2:上传文件的inputStream
		boolean bool = ftpClient.storeFile( serverUUIDName, fileInputStream);
		// 关闭连接
		fileInputStream.close();
		ftpClient.logout();
		if(!bool){ 
			delete(folder,serverUUIDName);
			throw new Exception("ftp上传失败!");
		}
		SysFile sysFile = new SysFile();
		sysFile.setFileName(UUIDName);
		sysFile.setFilePath(folder + datePath);
		sysFile.setFileCode(iSequenceService.getSequence(iRequest,"FILE_UPLOAD_CODE"));
		sysFile.setFileSize(file.getSize());
		sysFile.setFileType(file.getContentType());
		sysFile.setUploadDate(new Date());
		return sysFile;
	}

	// 生成uuid文件名
	private String uuidName(long fileSize,String fileName) {
		//UUID uuid = UUID.randomUUID();
		String date = new DateTime().toString("yyyyMMddHHmmssSss");
		return date + "_" + fileSize +"_"+ fileName;
	}

	/**
	 * ftp下载
	 *@param fileName 文件所在服务器路径 如:invoice/template.xlsx
	 *@param newName 下载到本地文件(中文)别名(不要后缀) 如:发票导入模板
	 */
	@Override
	public void download(HttpServletRequest request,HttpServletResponse response,String filePath,String fileName,String newName) throws Exception {
		if(StringUtils.isBlank(filePath)){
			throw new Exception("filePath can't be null");
		}
		// 创建一个FtpClient对象
		FTPClient ftpClient = connect();
		InputStream is = null;
		OutputStream os = null;
		try {
			ftpClient.changeWorkingDirectory(ftp_basepath + filePath);
			is = ftpClient.retrieveFileStream(fileName);
			if (is == null) {
				is = ftpClient.retrieveFileStream(new String(fileName.getBytes(), "iso-8859-1"));
			}
			if(is==null)throw new Exception("下载文件失败!");
			String end = fileName.substring(fileName.lastIndexOf("."));
			String name = StringUtils.isBlank(newName)?fileName:(newName+end);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition","attachment;filename=" + new String(name.getBytes("gbk"), "iso-8859-1"));
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "max-age=0");
			
			os = response.getOutputStream();
			byte[] buffer = new byte[1024*256];
			int b = -1;
			while ((b = is.read(buffer)) != -1) {
				os.write(buffer, 0, b);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if(is!=null){
				is.close();
			}
			if(os!=null){
				os.flush();
				os.close();
			}
			ftpClient.logout();
		}
	}

	/**
	 * ftp删除
	 *@param folder 文件所在服务器上的目录
	 *@param fileName 服务器上的文件名
	 */
	@Override
	public boolean delete(String folder, String fileName) throws Exception {
		// 创建一个FtpClient对象
		FTPClient ftpClient = connect();
		boolean bool = false;
		if (ftpClient != null) {
			ftpClient.changeWorkingDirectory(ftp_basepath + "/" + folder);
			bool = ftpClient.deleteFile(new String(fileName.getBytes(), "iso-8859-1"));
			ftpClient.logout();
		}
		return bool;
	}
	
	@Override
	public List<FileMenuItem> getServerFolderMenu(){
		List<FileMenuItem> list = new ArrayList<>();
		try {
			// 创建一个FtpClient对象
			FTPClient ftpClient = connect();
			list = getFolderMenu("",ftpClient,ftp_basepath);
			ftpClient.logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 递归获取文件目录菜单
	 * @param parentFolder
	 * @param ftpClient
	 * @param basePath
	 * @return
	 * @throws Exception
	 */
	private List<FileMenuItem> getFolderMenu(String parentFolder,FTPClient ftpClient,String basePath) throws Exception{
		List<FileMenuItem> list = new ArrayList<>();
		if (ftpClient != null) {
			ftpClient.changeWorkingDirectory(basePath);
			FTPFile[] listFiles = ftpClient.listFiles();
			
			for (FTPFile ftpFile : listFiles) {
				String path = basePath;
				FileMenuItem item = new FileMenuItem();
				
				// 过滤掉数字格式的目录
				if(ftpFile.isDirectory()){
					if(!ftp_numforder && StringUtils.isNumeric(ftpFile.getName())){
					}else{
						path += "/"+ ftpFile.getName();
						item.setFolderName(ftpFile.getName());
						if(StringUtils.isNoneBlank(parentFolder)){
							item.setParentForlder(parentFolder);
						}
						item.setChildrenFolder(getFolderMenu(ftpFile.getName(),ftpClient,path));
						item.setBaseFolder(path.replace(ftp_basepath, ""));
						list.add(item);
					}
				}
			}
		}
		return list;
	}
	
}
