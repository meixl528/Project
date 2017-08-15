package com.ssm.attachment.impl;

import java.io.File;

import com.ssm.attachment.FileInfo;

/**
 * fileInfo的默认实现.
 */
public class DefaultFileInfo implements FileInfo {

    private File file = null;

    private String status = null;

    private String originalName = null;

    private String contentType = null;

    private String url;

    public File getFile() {
        return this.file;
    }

    public String getStatus() {
        return this.status;
    }


    public String getContentType() {
        return this.contentType;
    }

    public void setFile(File f) {
        this.file = f;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

}