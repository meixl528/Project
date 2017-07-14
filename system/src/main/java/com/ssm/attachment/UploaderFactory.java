package com.ssm.attachment;

import com.ssm.attachment.impl.StandardUploader;

/**
 * 上传类的工厂方法.
 */
public abstract class UploaderFactory {

    /**
     * 多文件上传实例.
     * 
     * @return Uploader StandardUploader
     */
    public static Uploader getMutiUploader() {
        return new StandardUploader();
    }

    /**
     * 单文件断点续传实例.
     * 
     * @return Uploader 待实现
     */
    public static Uploader getSingleUploader() {
        return null;
    }

}
