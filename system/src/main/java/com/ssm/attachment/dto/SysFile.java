package com.ssm.attachment.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import com.ssm.sys.dto.BaseDTO;

/**
 * 文件DTO.
 */
@Table(name = "tb_file")
public class SysFile extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @GeneratedValue(generator = GENERATOR_TYPE)
    private Long fileId;

    /**
     * 对应的附件id.
     */
    @Column
    private Long attachmentId;
    
    @Column
    private String fileCode;

    /**
     * 文件名称.
     */
    @Column
    @NotEmpty
    private String fileName;

    /**
     * 文件相对项目的虚拟路径.
     */
    @Column
    private String filePath;

    /**
     * 文件大小.
     */
    @Column
    private Long fileSize;

    /**
     * 文件类型.
     */
    @Column
    private String fileType;

    /**
     * 上传日期.
     */
    @Column
    private Date uploadDate;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode;
	}

	public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

}
