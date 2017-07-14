package com.ssm.attachment.service;

import java.io.IOException;
import java.util.Locale;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * 附件上传
 */
public interface IAttachmentProvider {
	String getAttachListHtml(String sourceType, String sourceKey, Locale locale, String contextPath)
			throws IOException, TemplateException;

	String getAttachHtml(String sourceType, String sourceKey, Locale locale, String contextPath, boolean enableRemove,
                         boolean enableUpload) throws IOException, TemplateException;

	void setConfiguration(Configuration configuration);
}
