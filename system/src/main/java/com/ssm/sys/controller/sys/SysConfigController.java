package com.ssm.sys.controller.sys;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ssm.attachment.exception.StoragePathNotExsitException;
import com.ssm.attachment.exception.UniqueFileMutiException;
import com.ssm.core.exception.TokenException;
import com.ssm.core.request.IRequest;
import com.ssm.sys.controller.BaseController;
import com.ssm.sys.dto.SysConfig;
import com.ssm.sys.responceFactory.ResponseData;
import com.ssm.sys.service.ISysConfigService;

/**
 * @name        SysConfigController
 * @description 系统配置
 * @author      meixl
 * @date        2017年8月8日下午2:52:39
 * @version
 */
@Controller
public class SysConfigController extends BaseController {

    @Autowired
    private ISysConfigService configService;

    /**
     * 配置信息查询.
     * 
     * @param config
     *            Config
     * @param page
     *            起始页
     * @param pagesize
     *            分页大小
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/config/query")
    @ResponseBody
    public ResponseData getConfig(HttpServletRequest request, SysConfig config,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(configService.select(iRequest, config, page, pagesize));
    }

    /**
     * 配置信息保存.
     * 
     * @param config
     *            config
     * @param result
     *            BindingResult
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     * @throws TokenException
     */
    @RequestMapping(value = "/sys/config/submit", method = RequestMethod.POST)
    public ResponseData submitConfig(@RequestBody List<SysConfig> config, BindingResult result,
            HttpServletRequest request) throws TokenException {
        getValidator().validate(config, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestCtx = createRequestContext(request);
        for(int i=0;i<config.size();i++)
        {
        	if(config.get(i).getConfigCode().equals("PASSWORD_MIN_LENGTH")){
        		
        		if(Integer.parseInt(config.get(i).getConfigValue())<6){
        			config.get(i).setConfigValue("6");
        		}else if(Integer.parseInt(config.get(i).getConfigValue())>16){
        			config.get(i).setConfigValue("16");
        		}
        	}
        }
        return new ResponseData(configService.batchUpdate(requestCtx, config));
    }

    /**
     * 图片上传提交页面.
     * 
     * @param request
     *            HttpServletRequest
     * @return Map 返回结果对象
     * @throws StoragePathNotExsitException
     *             存储路径不存在异常
     * @throws UniqueFileMutiException
     *             附件不唯一异常
     * @throws IOException
     * @throws FileUploadException
     */
    @RequestMapping(value = "/sys/config/uploadLogo", method = RequestMethod.POST, produces = "text/html")
    public String uploadLogo(HttpServletRequest request)
            throws StoragePathNotExsitException, UniqueFileMutiException, IOException, FileUploadException {
        String file_path = request.getServletContext().getRealPath("/") + "/resources/upload";
        String fileName = "logo.png";
        File dir=new File(file_path);
        if(!dir.exists())
            dir.mkdir();
        File tempFile = new File(file_path+'/'+fileName);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);

        for (FileItem fi : items) {
            if (fi.isFormField()) {
                fi.getFieldName();
                fi.getString();
            } else {
                String imgName = fi.getName();//
                if (imgName == null) {
                    return "<script>window.parent.showUploadError('NO_FILE')</script>";
                } else {
                    int idx = imgName.lastIndexOf(".");
                    if (idx != -1) {
                        String ext = imgName.substring(idx + 1).toUpperCase();
                        ext = ext.toLowerCase();
                        if (!ext.equals("jpg") && !ext.equals("png") && !ext.equals("jpeg") && !ext.equals("gif")) {
                            // 错误信息
                            return "<script>window.parent.showUploadError('FILE_TYPE_ERROR')</script>";
                        }
                    } else {
                        // 文件类型错误
                        return "<script>window.parent.showUploadError('FILE_NO_SUFFIX')</script>";
                    }
                }
                try (InputStream is = fi.getInputStream(); OutputStream os = new FileOutputStream(tempFile)) {
                    IOUtils.copyLarge(is, os);
                }

            }
        }

        return "<script>window.parent.showUploadSucessLogo()</script>";
    }

    /**
     * 图片上传提交页面.
     * 
     * @param request
     *            HttpServletRequest
     * @return Map 返回结果对象
     * @throws StoragePathNotExsitException
     *             存储路径不存在异常
     * @throws UniqueFileMutiException
     *             附件不唯一异常
     * @throws IOException
     * @throws FileUploadException
     */
    @RequestMapping(value = "/sys/config/uploadIcon", method = RequestMethod.POST, produces = "text/html")
    public String uploadIcon(HttpServletRequest request)
            throws StoragePathNotExsitException, UniqueFileMutiException, IOException, FileUploadException {
        String file_path = request.getServletContext().getRealPath("/") + "/resources/upload/";
        String fileName = "favicon.png";
      
        File dir=new File(file_path);
        if(!dir.exists())
            dir.mkdir();
        
        File tempFile = new File(file_path+'/'+fileName);
        
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);

        for (FileItem fi : items) {
            if (fi.isFormField()) {
                fi.getFieldName();
                fi.getString();
            } else {
                String imgName = fi.getName();//
                if (imgName == null) {
                    return "<script>window.parent.showUploadError('NO_FILE')</script>";
                } else {
                    int idx = imgName.lastIndexOf(".");
                    if (idx != -1) {
                        String ext = imgName.substring(idx + 1).toUpperCase();
                        ext = ext.toLowerCase();
                        if (!ext.equals("jpg") && !ext.equals("png") && !ext.equals("jpeg") && !ext.equals("gif")) {
                            // 错误信息
                            return "<script>window.parent.showUploadError('FILE_TYPE_ERROR')</script>";
                        }
                    } else {
                        // 文件类型错误
                        return "<script>window.parent.showUploadError('FILE_NO_SUFFIX')</script>";
                    }
                }
                try (InputStream is = fi.getInputStream(); OutputStream os = new FileOutputStream(tempFile)) {
                    IOUtils.copyLarge(is, os);
                }

            }
        }

        return "<script>window.parent.showUploadSucessFavicon()</script>";
    }
}