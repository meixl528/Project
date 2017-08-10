package com.ssm.attachment.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ssm.attachment.ContentTypeFilter;
import com.ssm.attachment.Controller;
import com.ssm.attachment.FileChain;
import com.ssm.attachment.FileInfo;
import com.ssm.attachment.FileProcessor;
import com.ssm.attachment.UpConstants;
import com.ssm.attachment.Uploader;

/**
 * 标准的上传组件.
 */
public class StandardUploader implements Uploader {

    private static Logger logger = LoggerFactory.getLogger(StandardUploader.class);

    private HttpServletRequest request = null;

    private Map<String, String> params = new HashMap<String, String>();

    private StandardFileChain chain = null;

    private Controller controller = null;
    /**
     * contentType过滤器.
     **/
    private ContentTypeFilter filter;
    /**
     * 文件信息.
     */
    private List<FileInfo> fileInfos;
    /**
     * 文件信息.
     */
    private List<FileItem> fileItems = new ArrayList<FileItem>();
    /**
     * 允许单个文件上传的大小.
     */
    private long singleFileSize = UpConstants.SINGLE_FILE_SIZE_MAX;
    /**
     * 允许总共文件上传的大小.
     */
    private long allFileSize = UpConstants.ALL_FILE_SIZE;
    /**
     * 允许文件上传个数.
     */
    private int maxFileNum = UpConstants.MAX_FILE_NUM;
    /**
     * 上传成功.
     */
    private String status = UpConstants.SUCCESS;

    private boolean isMultiPartFiled;

    @SuppressWarnings("unchecked")
	public void init(HttpServletRequest request) {
        long allSize = 0;
        int fileNum = 0;
        this.request = request;
        this.fileInfos = new ArrayList<FileInfo>();
        isMultiPartFiled = request instanceof MultipartHttpServletRequest;
        // 如果没有设置，就设置标准的filter
        if (filter == null) {
            filter = new DefaultContentTypeFilter();
        }
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            // 不是文件上传
            if (!isMultipart) {
                status = UpConstants.NOT_FILE_ERROR;
                return;
            }
            // 初始化执行链
            chain = new StandardFileChain(fileInfos, this);

            if (isMultiPartFiled) {
                MultipartFile multipartFile = ((MultipartHttpServletRequest) request).getFile("files");
                processFormField("sourceType", request.getParameter("sourceType"));
                processFormField("sourceKey", request.getParameter("sourceKey"));
                fileItems.add(new MultipartFiledByFileItem(multipartFile));
            } else {
                ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                List<FileItem> items = upload.parseRequest(request);
                // 先判断这次文件上传总体是否符合要求
                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        allSize += item.getSize();
                        fileNum += 1;
                    }
                }

                if (allSize > this.allFileSize) {
                    status = UpConstants.ALL_SIZE_MAX_ERROR;
                    return;
                } else if (fileNum > this.maxFileNum) {
                    status = UpConstants.LIMIT_UPLOADNUM_ERROR;
                    return;
                }
                // 处理表单
                Iterator<FileItem> iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = iter.next();
                    if (item.isFormField()) {
                        processFormField(item);
                    } else if (!item.isFormField()) {
                        fileItems.add(item);
                    }
                }
            }
            // 捕捉异常
        } catch (FileUploadException e) {
            status = UpConstants.UPLOAD_IO_ERROR;
            if (logger.isErrorEnabled()) {
                logger.error("文件上传错误", e);
            }
        } catch (IOException e) {
            status = UpConstants.UPLOAD_IO_ERROR;
            if (logger.isErrorEnabled()) {
                logger.error("文件IO错误", e);
            }
        } catch (Exception e) {
            status = UpConstants.UPLOAD_ERROR;
            if (logger.isErrorEnabled()) {
                logger.error("文件上传过程中发生错误", e);
            }
        }
    }

    public List<FileInfo> upload() {
        if (controller == null) {
            controller = new StandardController();
        }
        if (fileItems != null && fileItems.size() > 0) {
            for (FileItem f : fileItems) {
                try {
                    processUploadedFile(f);
                } catch (Exception e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("文件上传发生错误", e);
                    }
                    status = UpConstants.UPLOAD_ERROR;
                }
            }
        }
        try {
            if (chain instanceof FileChain) {
                chain.doProcess();
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("文件处理发生错误", e);
            }
            status = UpConstants.FILE_PROCESSE_ERROR;
        }
        // 处理执行链
        return fileInfos;
    }

    // 验证文件是否符合上传规范
    private String validate(FileItem fileItem) {
        String fileName = getFileName(fileItem);
        // 验证文件大小
        if (fileItem.getSize() <= 0 || StringUtils.isEmpty(fileName)) {
            // 空文件
            return UpConstants.FILE_EMPTY_ERROR;
        }
        // 文件单个超过大小
        if (fileItem.getSize() > this.singleFileSize) {
            return UpConstants.SINGLE_FILE_SIZE_MAX_ERROR;
        }
        // 总文件大小超出
        if (logger.isDebugEnabled()) {
            logger.debug("上传文件名为：{}  =====> 其 contentType: {} ", fileName, fileItem.getContentType());

        }
        // contentType 不接受
        if (!filter.isAccept(fileItem.getName(), fileItem.getContentType())) {
            return UpConstants.FILE_DISALLOWD_ERROR;
        }
        return UpConstants.SUCCESS;
    }

    private String getFileName(FileItem item) {
        String fileName = item.getName();
        if (fileName != null) {
            int index = fileName.lastIndexOf("\\");
            if (index != -1) {
                fileName = fileName.substring(index + 1);
            }
        }
        return fileName;
    }

    private void processUploadedFile(FileItem item) {
        if (!item.isFormField()) {
            // 封装文件信息
            DefaultFileInfo fileInfo = new DefaultFileInfo();
            String fileName = getFileName(item);

            fileInfo.setOriginalName(fileName);
            fileInfo.setContentType(item.getContentType());
            String result = validate(item);
            if (!result.equals(UpConstants.SUCCESS)) {
                status = UpConstants.UPLOAD_ERROR;
                fileInfo.setStatus(result);
                fileInfo.setFile(null);
                fileInfo.setUrl(null);
                fileInfos.add(fileInfo);
                return;
            }
            try {
                File f = new File(controller.getFileDir(request, fileName) + controller.newName(fileName));
                item.write(f);
                fileInfo.setFile(f);
                fileInfo.setStatus(UpConstants.SUCCESS);
                fileInfo.setUrl(controller.urlFix(request, f));
                if (logger.isDebugEnabled()) {
                    logger.debug(f.getAbsolutePath());
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("文件上传错误！", e);
                }
                fileInfo.setStatus(UpConstants.UPLOAD_ERROR);
                this.status = UpConstants.UPLOAD_ERROR;
            }
            fileInfos.add(fileInfo);
        }
    }

    private void processFormField(FileItem item) throws IOException {
        if (item.isFormField()) {
            String name = item.getFieldName();
            String value = new String(item.getString().getBytes("ISO-8859-1"), UpConstants.CHARSET_UTF);
            params.put(name, value);
        }
    }

    private void processFormField(String name, String value) throws IOException {
        value = new String(value.getBytes("ISO-8859-1"), UpConstants.CHARSET_UTF);
        params.put(name, value);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void addFileProcessor(FileProcessor processor) {
        chain.addProcessor(processor);
    }

    public void doProcess() throws Exception {
    }

    public String getParams(String key) {
        return params.get(key);
    }

    public void setParams(String key, String value) {
        request.setAttribute(key, value);
    }

    public void setSingleFileSize(long singleFileSize) {
        this.singleFileSize = singleFileSize;
    }

    public void setAllFileSize(long allFileSize) {
        this.allFileSize = allFileSize;
    }

    public void setMaxFileNum(int maxFileNum) {
        this.maxFileNum = maxFileNum;
    }

    public String getStatus() {
        return status;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public void setFilter(ContentTypeFilter filter) {
        this.filter = filter;
    }

    public static class MultipartFiledByFileItem implements FileItem {
        MultipartFile multipartFile = null;

        MultipartFiledByFileItem(MultipartFile multipartFile) {
            this.multipartFile = multipartFile;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return multipartFile.getInputStream();
        }

        @Override
        public String getContentType() {
            return multipartFile.getContentType();
        }

        @Override
        public String getName() {
            return multipartFile.getOriginalFilename();
        }

        @Override
        public boolean isInMemory() {
            return false;
        }

        @Override
        public long getSize() {
            return multipartFile.getSize();
        }

        @Override
        public byte[] get() {
            return new byte[0];
        }

        @Override
        public String getString(String s) throws UnsupportedEncodingException {
            return null;
        }

        @Override
        public String getString() {
            return null;
        }

        @Override
        public void write(File file) throws Exception {
            multipartFile.transferTo(file);
        }

        @Override
        public void delete() {

        }

        @Override
        public String getFieldName() {
            return multipartFile.getName();
        }

        @Override
        public void setFieldName(String s) {

        }

        @Override
        public boolean isFormField() {
            return false;
        }

        @Override
        public void setFormField(boolean b) {

        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

		@Override
		public FileItemHeaders getHeaders() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setHeaders(FileItemHeaders headers) {
			// TODO Auto-generated method stub
		}

    }
}
