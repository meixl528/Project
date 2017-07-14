/*
 * #{copyright}#
 */
package com.ssm.attachment.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssm.attachment.FileInfo;
import com.ssm.attachment.UpConstants;
import com.ssm.attachment.Uploader;
import com.ssm.attachment.UploaderFactory;
import com.ssm.attachment.dto.AttachCategory;
import com.ssm.attachment.dto.Attachment;
import com.ssm.attachment.dto.SysFile;
import com.ssm.attachment.exception.FileReadIOException;
import com.ssm.attachment.exception.StoragePathNotExsitException;
import com.ssm.attachment.exception.UniqueFileMutiException;
import com.ssm.attachment.service.IAttachCategoryService;
import com.ssm.attachment.service.ISysFileService;
import com.ssm.core.BaseConstants;
import com.ssm.core.exception.TokenException;
import com.ssm.core.request.IRequest;
import com.ssm.core.util.UploadUtil;
import com.ssm.sys.controller.BaseController;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 附件管理器.
 */
@Controller
public class AttachmentController extends BaseController {

    /**
     * 提示信息名.
     */
    private static final String MESSAGE_NAME = "message";

    /**
     * 提示只能上传一个文件
     */
    private static final String MESG_UNIQUE = "Unique";
    /**
     * 提示成功.
     */
    private static final String MESG_SUCCESS = "success";
    /**
     * 文件不存在提示.
     */
    private static final String FILE_NOT_EXSIT = "file_not_exsit";
    /**
     * 提示信息 name.
     **/
    private static final String INFO_NAME = "info";
    /**
     * 附件上传存储目录未分配.
     **/
    private static final String TYPEORKEY_EMPTY = "TYPEORKEY_EMPTY";
    /**
     * sourceType 错误.
     */
    private static final String TYPE_ERROR = "SOURCETYPE_ERROR";
    /**
     * 数据库 错误.
     */
    private static final String DATABASE_ERROR = "DATABASE_ERROR";
    /**
     * 图片mime前缀.
     */
    private static final String IMAGE_MIME_PREFIX = "image";
    /**
     * file对象名.
     */
    private static final String FILE_NAME = "file";
    /**
     * buffer 大小.
     */
    private static final Integer BUFFER_SIZE = 1024;

    /**
     * 图片压缩大小.
     */
    private static final Float COMPRESS_SIZE = 40f;

    /**
     * 进制单位.
     */
    private static final Float BYTE_TO_KB = 1024f;
    /**
     * 文件下载默认编码.
     */
    private static final String ENC = "UTF-8";

    /**
     * 日志记录.
     **/
    private static Logger logger = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    private IAttachCategoryService categoryService;
    @Autowired
    private ISysFileService fileService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 附件列表.
     *
     * @param request    HttpServletRequest
     * @param sourceType 关联业务code
     * @param sourceKey  关联业务表主健
     * @return ModelAndView 视图(/attach/sys_attach_manage)
     */
    @RequestMapping(value = "/sys/attach/manage", method = RequestMethod.GET)
    public ModelAndView manager(HttpServletRequest request, String sourceType, String sourceKey) {
        request.setAttribute("sourceType", sourceType);
        request.setAttribute("sourceKey", sourceKey);
        IRequest requestContext = createRequestContext(request);
        AttachCategory category = categoryService.selectAttachByCode(requestContext, sourceType);
        if (category != null) {
            request.setAttribute("files", fileService.selectFilesByTypeAndKey(requestContext, sourceType, sourceKey));
            request.setAttribute(MESSAGE_NAME, MESG_SUCCESS);
        }
        return new ModelAndView("/attach/sys_attach_manage");
    }

    /**
     * 附件上传提交页面.
     *
     * @param request HttpServletRequest
     * @return Map 返回结果对象
     * @throws StoragePathNotExsitException 存储路径不存在异常
     * @throws UniqueFileMutiException      附件不唯一异常
     * @throws JsonProcessingException
     */

    @RequestMapping(value = "/sys/attach/upload", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
    public String upload(HttpServletRequest request, Locale locale, String contextPath)
            throws StoragePathNotExsitException, UniqueFileMutiException, JsonProcessingException {
        Map<String, Object> response = new HashMap<String, Object>();
        Uploader uploader = UploaderFactory.getMutiUploader();
        uploader.init(request);
        String sourceType = uploader.getParams("sourceType");
        String sourceKey = uploader.getParams("sourceKey");
        response.put("success", false);
        if (StringUtils.isBlank(sourceType) || StringUtils.isBlank(sourceKey)) {
            response.put(MESSAGE_NAME, messageSource.getMessage("hap.typeorkey_empty", null, locale));// TYPEORKEY_EMPTY
            response.put(INFO_NAME, messageSource.getMessage("hap.typeorkey_empty", null, locale));
            return objectMapper.writeValueAsString(response);
        }
        IRequest requestContext = createRequestContext(request);
        // 设置上传参数
        AttachCategory category = UploadUtil.initUploaderParams(uploader, sourceType, requestContext, categoryService);
        // TYPE 设置不对
        if (category == null) {
            response.put(MESSAGE_NAME, messageSource.getMessage("hap.type_error", null, locale));
            response.put(INFO_NAME, messageSource.getMessage("hap.type_error", null, locale));
            return objectMapper.writeValueAsString(response);
        }
        List<FileInfo> fileInfos = uploader.upload();
        // 出错了
        if (!UpConstants.SUCCESS.equals(uploader.getStatus())) {
            response.put(MESSAGE_NAME, fileInfos.get(0).getStatus());
            response.put(INFO_NAME, fileInfos.get(0).getStatus());
            return objectMapper.writeValueAsString(response);
        } else {
            response.put("success", true);
            response.put(MESSAGE_NAME, messageSource.getMessage("hap.upload_success", null, locale));// uploader.getStatus()
        }
        // WebUploader 每次只上传一个文件
        FileInfo f = fileInfos.get(0);
        try {
            SysFile sysFile = UploadUtil.genSysFile(f, requestContext.getUserId(), requestContext.getUserId());
            Attachment attach = UploadUtil.genAttachment(category, sourceKey, requestContext.getUserId(),
                    requestContext.getUserId());
            // 分类如果是唯一类型
            if (BaseConstants.YES.equals(category.getIsUnique())) {
                sysFile = fileService.updateOrInsertFile(requestContext, attach, sysFile);
            } else {
                fileService.insertFileAndAttach(requestContext, attach, sysFile);
            }
            sysFile.setFilePath(null);
            //TokenUtils.generateAndSetToken(TokenUtils.getSecurityKey(request.getSession(false)), sysFile);
            response.put(FILE_NAME, sysFile);
        } catch (UniqueFileMutiException ex) {
            response.put("success", false);
            response.put(MESSAGE_NAME, messageSource.getMessage("hap.mesg_unique", null, locale));
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("database error", e);
            }
            File file = f.getFile();
            if (file.exists()) {
                file.delete();
            }
            response.put("success", false);
            response.put(MESSAGE_NAME, messageSource.getMessage("hap.database_error", null, locale));
            response.put(INFO_NAME, messageSource.getMessage("hap.database_error", null, locale));
        }
        return objectMapper.writeValueAsString(response);
    }

    /**
     * 文件删除.
     *
     * @param request HttpServletRequest
     * @param fileId  文件id
     * @param token   token
     * @return Map 结果对象
     * @throws TokenException
     */
    @RequestMapping(value = "/sys/attach/remove")
    public Map<String, Object> remove(HttpServletRequest request, String fileId, String token) throws TokenException {

        Map<String, Object> response = new HashMap<String, Object>();
        IRequest requestContext = createRequestContext(request);
        SysFile file = new SysFile();
        file.setFileId(Long.valueOf(fileId));
        file.set_token(token);
        //TokenUtils.checkToken(request.getSession(false), file);
        file = fileService.delete(requestContext, file);
        response.put(MESSAGE_NAME, MESG_SUCCESS);
        return response;
    }

    /**
     * 具体查看某个附件.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param fileId   文件id
     * @throws FileReadIOException 文件读取IO异常
     */
    @RequestMapping(value = "/sys/attach/file/detail")
    public void detail(HttpServletRequest request, HttpServletResponse response, String fileId, String token)
            throws FileReadIOException, TokenException {
        IRequest requestContext = createRequestContext(request);
        SysFile sysFile = fileService.selectByPrimaryKey(requestContext, Long.valueOf(fileId));
        sysFile.set_token(token);
        //TokenUtils.checkToken(request.getSession(false), sysFile);
        try {
            if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
                File file = new File(sysFile.getFilePath());
                if (file.exists()) {
                    // 下载
//                    if (StringUtils.isNotBlank(sysFile.getFileType())
//                            && sysFile.getFileType().startsWith(IMAGE_MIME_PREFIX)) {
                    response.addHeader("Content-Disposition",
                            "attachment;filename=\"" + URLEncoder.encode(sysFile.getFileName(), ENC) + "\"");
//                    }
                    response.setContentType(sysFile.getFileType() + ";charset=" + ENC);
                    response.setHeader("Accept-Ranges", "bytes");
                    int fileLength = (int) file.length();
                    response.setContentLength(fileLength);
                    if (fileLength > 0) {
                        writeFileToResp(response, file);
                    }
                } else {
                    response.getWriter().write(FILE_NOT_EXSIT);
                }
            } else {
                response.getWriter().write(FILE_NOT_EXSIT);
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        }

    }

    /**
     * 查看某个附件.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param fileId   文件id
     * @param compress 是否压缩
     * @throws FileReadIOException IO exception
     */
    @RequestMapping(value = "/sys/attach/file/view")
    public void view(HttpServletRequest request, HttpServletResponse response, String fileId, String compress)
            throws FileReadIOException {
        IRequest requestContext = createRequestContext(request);
        SysFile sysFile = fileService.selectByPrimaryKey(requestContext, Long.valueOf(fileId));
        try {
            if (sysFile != null && StringUtils.isNotBlank(sysFile.getFilePath())) {
                // 在contextpath和path之间多一个File.separator
                File file = new File(sysFile.getFilePath());
                if (file.exists()) {
                    // response.setContentType(sysFile.getFileType() +
                    // ";charset=UTF-8");
                    response.setHeader("cache-control", "must-revalidate");
                    response.setHeader("pragma", "public");
                    response.setHeader("Content-Type", sysFile.getFileType());
                    response.setHeader("Accept-Ranges", "bytes");
                    response.setHeader("Content-disposition",
                            "attachment;" + processFileName(request, sysFile.getFileName()));

                    int fileLength = (int) file.length();
                    response.setContentLength(fileLength);
                    if (fileLength > 0) {
                        if (StringUtils.isNotBlank(compress) && BaseConstants.YES.equals(compress)) {
                            try (OutputStream os = response.getOutputStream()) {
                                Thumbnails.of(file).scale(getCompressPercent(fileLength)).toOutputStream(os);
                                os.flush();
                            }
                        } else {
                            writeFileToResp(response, file);
                        }
                    }
                } else {
                    response.getWriter().write(FILE_NOT_EXSIT);
                }
            } else {
                response.getWriter().write(FILE_NOT_EXSIT);
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        }
    }

    private String processFileName(HttpServletRequest request, String filename) throws UnsupportedEncodingException {
        String userAgent = request.getHeader("User-Agent");
        String new_filename = URLEncoder.encode(filename, "UTF8");
        String rtn = "filename=\"" + new_filename + "\"";
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if (userAgent.indexOf("msie") != -1) {
                rtn = "filename=\"" + new String(filename.getBytes("GB2312"), "ISO-8859-1") + "\"";
            } else if (userAgent.indexOf("safari") != -1 || userAgent.indexOf("applewebkit") != -1) {
                rtn = "filename=\"" + new String(filename.getBytes("UTF-8"), "ISO8859-1") + "\"";
            } else if (userAgent.indexOf("opera") != -1 || userAgent.indexOf("mozilla") != -1) {
                rtn = "filename*=UTF-8''" + new_filename;
            }
        }
        return rtn;
    }

    /**
     * 将文件对象的流写入Responsne对象.
     *
     * @param response HttpServletResponse
     * @param file     File
     * @throws FileNotFoundException 找不到文件异常
     * @throws IOException           IO异常
     */
    private void writeFileToResp(HttpServletResponse response, File file) throws FileNotFoundException, IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        try (InputStream inStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int readLength;
            while (((readLength = inStream.read(buf)) != -1)) {
                outputStream.write(buf, 0, readLength);
            }
            outputStream.flush();

        }
    }

    /**
     * 得到压缩比压缩大约至40KB的样子.
     *
     * @param len 图片文件长度
     * @return float 图片压缩比
     */
    private float getCompressPercent(long len) {
        return (float) len / (BYTE_TO_KB * COMPRESS_SIZE);
    }

}
