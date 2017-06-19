package testFTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.github.pagehelper.StringUtil;

public class TestFTP {
	
	
	//@Value("${FTP_IP}")
	private final static String ftp_ip = "192.168.10.100";
	//@Value("${FTP_PORT}")
	private static int ftp_port = 21;
	//@Value("${FTP_NAME}")
	private static String ftp_name ="ftpuser";
	//@Value("${FTP_PASS}")
	private static String ftp_pass ="wangreat123456";
	//@Value("${FTP_BASEPATH}")
	private static String ftp_basepath ="/home/ftpuser/test";
	
	private static String DatePath = "/yyyy/MM/dd";
	//private static String DatePath = "";
	
	public static void main(String[] args) {
		//testUpload();
		
		//testDownLoad();
		
		if(delete("/invoice/excel/2017/05/02","608146e6-d63a-4ee0-a8b9-607fd4d3beb0_login.sql")){
			System.out.println("delete OK");
		};
	}
	
	//测试上传 
	public static void testUpload(){
		File file = new File("C:\\Users\\meixl\\Desktop\\login.sql");
		InputStream fileInputStream;
		Map<String, String> map = new HashMap<>();
		try {
			fileInputStream = new FileInputStream(file);
			map = uploadInputStream("/invoice/excel",fileInputStream,file.getName());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		map.forEach((k,v) -> {
			System.out.println(k + " : " + v);
		});
	}
	
	//测试下载
	public static void testDownLoad(){
		try {
			download("invoice/123.png","C:\\Users\\meixl\\Desktop\\888.png");
			System.out.println("testDownLoad OK");
		} catch (Exception e) {
			System.out.println("test失败");
		}
	}
	

	public static FTPClient connect() throws Exception {
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
	 * @param folder
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static Map<String, String> uploadFile(String folder,CommonsMultipartFile file) throws IOException, Exception{
		return uploadInputStream(folder,file.getInputStream(),file.getOriginalFilename());
	}
	
	/**
	 * ftp上传
	 *@param folder 文件上传到具体服务器上的目录下   如:invoice/excel
	 *@param file 上传的文件
	 */
	public static Map<String, String> uploadInputStream(String folder, InputStream fileInputStream,String fileName) throws IOException,Exception {
		Map<String, String> map = new HashMap<>();
		String datePath ="";
		if(!StringUtil.isEmpty(DatePath)){
			datePath = new DateTime().toString(DatePath);
		}
		// 创建一个FtpClient对象
		FTPClient ftpClient = connect();
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

		String UUIDName = new String(uuidName(fileName).getBytes(), "iso-8859-1");
		// 参数1:服务器端文档名 , 参数2:上传文件的inputStream
		boolean bool = ftpClient.storeFile( UUIDName, fileInputStream);
		// 关闭连接
		fileInputStream.close();
		ftpClient.logout();
		if(!bool){ 
			delete(folder+datePath,UUIDName);
			throw new Exception("ftp上传失败!");
		}
		map.put("folder", folder+datePath);
		map.put("fileName", UUIDName);
		return map;
	}

	// 生成uuid文件名
	private static String uuidName(String fileName) {
		UUID uuid = UUID.randomUUID();
		return uuid.toString() + "_" + fileName;
	}

	//@Autowired
	//private HttpServletResponse response;

	/**
	 * 下载
	 * @param fileNamePath 下载全路径   /invoice/123.png
	 * @param newNamePath  存放全路径   C:\Users\meixl\Desktop\999.png
	 * @throws Exception
	 */
	public static void download(String fileNamePath,String newNamePath) throws Exception{
		// 创建一个FtpClient对象
		FTPClient ftpClient = connect();
		InputStream is = null;
		OutputStream os = null;
		try{
				ftpClient.changeWorkingDirectory(ftp_basepath);
				is = ftpClient.retrieveFileStream(fileNamePath);
				if (is == null) {
					is = ftpClient.retrieveFileStream(new String(fileNamePath.getBytes(), "iso-8859-1"));
				}
				if(is==null)throw new Exception("下载链接失败!");
				
				File file = new File(newNamePath);
				os = new FileOutputStream(file);
				
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
	 * ftp下载
	 *@param fileName 文件所在服务器路径 如:invoice/template.xlsx
	 *@param newName 下载到本地文件(中文)别名(不要后缀) 如:发票导入模板
	 */
/*	public static void download(String fileName,String newName) throws Exception {
		// 创建一个FtpClient对象
		FTPClient ftpClient = connect();
		InputStream is = null;
		OutputStream os = null;
		try {
			ftpClient.changeWorkingDirectory(ftp_basepath);
			is = ftpClient.retrieveFileStream(fileName);
			if (is == null) {
				is = ftpClient.retrieveFileStream(new String(fileName.getBytes(), "iso-8859-1"));
			}
			if(is==null)throw new Exception("下载链接失败!");
			String end = fileName.substring(fileName.lastIndexOf("."));
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String((newName+end).getBytes("gbk"), "iso-8859-1"));
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
	}*/

	/**
	 * ftp删除
	 *@param folder 文件所在服务器上的目录
	 *@param fileName 服务器上的文件名
	 */
	public static boolean delete(String folder, String fileName){
		boolean bool = true;
		FTPFile[] names = null;
		try {
			// 创建一个FtpClient对象
			FTPClient ftpClient = connect();
			if (ftpClient != null) {
				ftpClient.changeWorkingDirectory(ftp_basepath + folder);
				if((names = ftpClient.listFiles())!=null){
					for (FTPFile name : names) {
						if(name.getName().equals(fileName))
							bool = ftpClient.deleteFile(fileName);
					}
				}
				ftpClient.logout();
			}
		} catch (Exception e) {
			bool = false;
			System.out.println("删除失败");
		}
		return bool;
	}

}
