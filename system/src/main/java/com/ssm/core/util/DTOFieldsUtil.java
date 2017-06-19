package com.ssm.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;

import com.ssm.sys.dto.BaseDTO;
/**
 * @description 获取dto中 标有某类型注解的字段
 * @author meixl	2017年4月10日下午5:14:45
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class DTOFieldsUtil {
	
	/**获取指定dto的指定注解的字段
	 * @param dto 实体类对象
	 * @param annotation 注解类型
	 */
	public static Field[] getFieldsWithAnnotation(Class<?> dto,Class annotation){
		List<Field> list = new ArrayList<>();
		Field[] fields = dto.getDeclaredFields();
		//ReflectionUtils.doWithFields(dto.getClass(), fields::add);
        for (Field f : fields) {
        	f.setAccessible(true);
        	Annotation an = f.getDeclaredAnnotation(annotation);
        	if(an!=null){
        		list.add(f);
			}
        }
        return list.size()>0?list.toArray(new Field[list.size()]):new Field[0];
	}

	/**获取指定dto的指定注解的字段
	 * @param dto 实体类对象
	 * @param annotation 注解类型
	 */
	public static Field[] getFieldsWithAnnotation(BaseDTO dto,Class annotation){
        List<Field> list = new ArrayList<>();
        Field[] fields = dto.getClass().getDeclaredFields();
        for (Field f : fields) {
        	f.setAccessible(true);
			Annotation an = f.getDeclaredAnnotation(annotation);
        	if(an!=null)
        		list.add(f);
        }
		return list.size()>0?list.toArray(new Field[list.size()]):new Field[0];
	}
	
	
	/**
	 * 获取dto字段名,转换成表字段列名
	 * @Description 例如  userName -> user_name
	 */
	public static String getColumnName(Field field) {
        Column col = field.getAnnotation(Column.class);
        if (col == null || StringUtils.isEmpty(col.name())) {
            return camelToUnderLine(field.getName());
        }
        return col.name();
    }
	
	public static String getColumnName(Class<?> clazz,String colName) {
		Field field = null;
		try {
			field = clazz.getDeclaredField(colName);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		if(field ==null) 
			return null;
        return getColumnName(field);
    }
	
	private static final Map<String, String> CAMEL_UL_MAP = new HashMap<>();
	/**
     * convert camel hump to under line case.
     * @param camel
     *            can not be null
     * @return under line case
     */
    public static String camelToUnderLine(String camel) {
        String ret = CAMEL_UL_MAP.get(camel);
        if (ret == null) {
            ArrayList<String> tmp = new ArrayList<>();
            int lastIdx = 0;
            for (int i = 0; i < camel.length(); i++) {
                if (Character.isUpperCase(camel.charAt(i))) {
                    tmp.add(camel.substring(lastIdx, i).toLowerCase());
                    lastIdx = i;
                }
            }
            tmp.add(camel.substring(lastIdx));
            ret = StringUtils.join(tmp, "_");
            CAMEL_UL_MAP.put(camel, ret);
        }
        return ret;
    }
	
}
