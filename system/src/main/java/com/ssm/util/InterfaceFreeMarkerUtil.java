package com.ssm.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ssm.fnd.dto.StyleTemplate;
import com.ssm.fnd.service.IStyleTemplateService;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * @name        InterfaceFreeMarkerUtil
 * @description 接口专用FreeMarker工具
 * @author      meixl
 */
public class InterfaceFreeMarkerUtil {
	private static Configuration freeMarkerConfig = null;
	
	private static void initConfiguration(){
		if(freeMarkerConfig == null){
			freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_21);
		}
	}
	
	/**
	 * 通过文件名向指定文件的FreeMarker模板绑定数据，将渲染的结果以String输出<br/>
	 * <b>如果可以，请使用另一个重载的方法，通过Redis加载模板，提高性能</b>
	 * 
	 * @param templateData 要绑定进模板的数据，可空
	 * @param aimFolderPath 模板所在的文件夹【以classpath:为根，例如:"/SAP"】
	 * @param templateFileName 模板的文件名全称【例如:"test.ftl"】
	 * @return FreeMarker渲染的结果,以String输出
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws TemplateException
	 */
	@Deprecated
	public static String processTemplateToString(Map<String, Object> templateData, String aimFolderPath, String templateFileName) throws IOException, URISyntaxException, TemplateException{
		//参数校验
		if(templateData == null){
			templateData = new HashMap<String, Object>();
		}
		if(StringUtils.isEmpty(aimFolderPath) || StringUtils.isEmpty(templateFileName)){
			throw new IllegalArgumentException("Path or File Name is EMPTY!");
		}
		if('/' != aimFolderPath.charAt(0)){
			aimFolderPath = '/' + aimFolderPath;
		}
		//模板加载
		InterfaceFreeMarkerUtil.initConfiguration();
		FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File(freeMarkerConfig.getClass().getClassLoader().getResource(aimFolderPath).toURI()));
		freeMarkerConfig.setTemplateLoader(fileTemplateLoader);
		//渲染
		StringWriter writer = new StringWriter(1024);
		Template template = freeMarkerConfig.getTemplate(templateFileName);
		template.process(templateData, writer);
		return writer.toString();
	}
	
	/**
	 * 通过templateCode向Redis中指定FreeMarker模板绑定数据，将渲染的结果以Map输出
	 * 
	 * @param templateData 要绑定进模板的数据，可空
	 * @param templateCode 模板代码
	 * @param templateService IStyleTemplateService的实例
	 * @return FreeMarker渲染的结果,以Map输出<br/>
	 * <b>key:template</b>->渲染完成的报文<br/>
	 * <b>key:writeToDb</b>->报文是否写到数据库[Y/N/null]<br/>
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static Map<String,String> processTemplateToString(Map<String, Object> templateData, String templateCode, IStyleTemplateService templateService) throws IOException, TemplateException{
		Map<String,String> map = new HashMap<>();
		//模板加载
		StyleTemplate templateMetaData = templateService.getStyleTemplate(templateCode);
		if(templateMetaData == null){
			throw new IllegalArgumentException("Invalid TemplateCode");
		}
		String templateString = templateMetaData.getTemplateContent();
		InterfaceFreeMarkerUtil.initConfiguration();
		//渲染
		StringWriter writer = new StringWriter(1024);
		Template template = new Template(templateCode, new StringReader(templateString), freeMarkerConfig);
		template.process(templateData, writer);
		map.put("template", writer.toString());
		map.put("writeToDb", templateMetaData.getLogFlag());
		return map;
	}
}
