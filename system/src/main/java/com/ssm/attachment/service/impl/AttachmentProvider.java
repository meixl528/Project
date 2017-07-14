package com.ssm.attachment.service.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.ssm.attachment.dto.AttachCategory;
import com.ssm.attachment.dto.SysFile;
import com.ssm.attachment.service.IAttachCategoryService;
import com.ssm.attachment.service.IAttachmentProvider;
import com.ssm.attachment.service.ISysFileService;
import com.ssm.core.request.impl.RequestHelper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class AttachmentProvider implements IAttachmentProvider {

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IAttachCategoryService attachCategoryService;

    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 生成UUID
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     *
     * @param sourceType
     * 附件来源类型
     * @param sourceKey
     * 附件索引
     * @param locale
     * @param contextPath
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String getAttachListHtml(String sourceType, String sourceKey, Locale locale, String contextPath)
            throws IOException, TemplateException {
        List<SysFile> files = sysFileService.queryFilesByTypeAndKey(RequestHelper.newEmptyRequest(), sourceType,
                sourceKey);
        AttachCategory category = attachCategoryService.selectAttachByCode(RequestHelper.newEmptyRequest(), sourceType);

        Template template = getConfiguration().getTemplate("Upload.ftl");

        try (StringWriter out = new StringWriter()) {
            Map<String,Object> param = new HashMap<>();
            param.put("file", files);
            param.put("fid", uuid());
            param.put("enableRemove", true);
            param.put("enableUpload", true);
            param.put("sourceType", sourceType);
            param.put("sourceKey", sourceKey);
            param.put("type", category.getAllowedFileType());
            param.put("size", category.getAllowedFileSize());
            param.put("unique", category.getIsUnique());
            param.put("filename", messageSource.getMessage("sysfile.filename", null, locale));
            param.put("filetype", messageSource.getMessage("sysfile.filetype", null, locale));
            param.put("filesize", messageSource.getMessage("sysfile.filesize", null, locale));
            param.put("upload", messageSource.getMessage("sysfile.uploaddate", null, locale));
            param.put("delete", messageSource.getMessage("hap.delete", null, locale));
            param.put("contextPath", contextPath);
            template.process(param, out);
            out.flush();
            String html = out.toString();
            return html;

        }

    }

    /**
     *
     * @param sourceType
     * @param sourceKey
     * @param locale
     * @param contextPath
     * @param enableRemove
     * 是否允许删除
     * @param enableUpload
     * 是否允许上传
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    @Override
    public String getAttachHtml(String sourceType, String sourceKey, Locale locale, String contextPath,
                                boolean enableRemove, boolean enableUpload) throws IOException, TemplateException {
        List<SysFile> files = sysFileService.queryFilesByTypeAndKey(RequestHelper.newEmptyRequest(), sourceType,
                sourceKey);
        AttachCategory category = attachCategoryService.selectAttachByCode(RequestHelper.newEmptyRequest(), sourceType);

        Template template = getConfiguration().getTemplate("Upload.ftl");

        try (StringWriter out = new StringWriter()) {
        	Map<String,Object> param = new HashMap<>();
            param.put("file", files);
            param.put("fid", uuid());
            param.put("enableRemove", enableRemove);
            param.put("enableUpload", enableUpload);
            param.put("sourceType", sourceType);
            param.put("sourceKey", sourceKey);
            param.put("type", category.getAllowedFileType());
            param.put("size", category.getAllowedFileSize());
            param.put("unique", category.getIsUnique());
            param.put("filename", messageSource.getMessage("sysfile.filename", null, locale));
            param.put("filetype", messageSource.getMessage("sysfile.filetype", null, locale));
            param.put("filesize", messageSource.getMessage("sysfile.filesize", null, locale));
            param.put("upload", messageSource.getMessage("sysfile.uploaddate", null, locale));
            param.put("delete", messageSource.getMessage("hap.delete", null, locale));
            param.put("contextPath", contextPath);
            template.process(param, out);
            out.flush();
            String html = out.toString();
            return html;
        }
    }

}
