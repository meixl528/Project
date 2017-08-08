package com.ssm.fnd.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @name        FileMenuItem
 * @description 服务器文件目录菜单
 * @author      meixl
 * @date        2017年8月7日上午9:51:15
 * @version
 */
public class FileMenuItem implements Serializable{

	/**
	 */
	private static final long serialVersionUID = -6967168348574203224L;
	
	private List<FileMenuItem> childrenFolder;
	
	private String folderName;
	
	private String parentForlder;
	
	private String baseFolder;

	public List<FileMenuItem> getChildrenFolder() {
		return childrenFolder;
	}

	public void setChildrenFolder(List<FileMenuItem> childrenFolder) {
		this.childrenFolder = childrenFolder;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getParentForlder() {
		return parentForlder;
	}

	public void setParentForlder(String parentForlder) {
		this.parentForlder = parentForlder;
	}

	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	
}
