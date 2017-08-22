package CleanMaven;

import java.io.File;

/**
 * @name        CleanMvn
 * @description 清除maven下载包失败后残留的文件
 * @author      meixl
 * @date        2017年8月22日下午5:07:00
 * @version
 */
public class CleanMvn {
	
	public static void main(String[] args){
		String mavenFile = "E:\\maven_3.2.5\\mvnRespo";
		
		delete(new File(mavenFile));
    }
	
	public static void delete(File file){
		if (file.exists()) {
			if(file.isFile()){
				//if (file.getName().endsWith(".lastUpdated")) {
				if (!file.getName().endsWith(".jar")) {
					file.delete();
				}
			}else if(file.isDirectory()){
				File[] files = file.listFiles();
				if(files.length==0){
					file.delete();
				}else{
					for (File f : files) {
						delete(f);
					}
				}
			}
		}
	}
	
}