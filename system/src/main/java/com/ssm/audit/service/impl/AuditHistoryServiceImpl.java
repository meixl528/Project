/*
 * #{copyright}#
 */

package com.ssm.audit.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.github.pagehelper.PageHelper;
import com.ssm.audit.mapper.AuditMapper;
import com.ssm.audit.service.IAuditHistoryService;
import com.ssm.audit.service.IAuditTableNameProvider;
import com.ssm.audit.util.AuditUtils;
import com.ssm.core.annotation.AuditEnabled;
import com.ssm.mybatis.entity.EntityField;
import com.ssm.sys.dto.BaseDTO;
import com.ssm.sys.dto.DTOClassInfo;
/**
 */
@Service
public class AuditHistoryServiceImpl implements IAuditHistoryService {

    private Logger logger = LoggerFactory.getLogger(AuditHistoryServiceImpl.class);

    @Autowired
    private AuditMapper auditMapper;

    @Autowired(required = false)
    private IAuditTableNameProvider auditTableNameProvider = DefaultAuditTableNameProvider.instance;

    public AuditHistoryServiceImpl() {
    }

    @Override
    public List<Map<String, Object>> selectAuditHistory(BaseDTO dto, int page, int pagesize)
            throws InvocationTargetException, NoSuchMethodException {
        PageHelper.startPage(page, pagesize);
        Map<String, Object> auditParam = new HashMap<>();
        Class clazz = dto.getClass();
        Table tbl = (Table) clazz.getAnnotation(Table.class);
        AuditEnabled auditEnabled = (AuditEnabled) clazz.getAnnotation(AuditEnabled.class);
        String baseTableName = tbl.name();
        String auditTableName = StringUtils.defaultIfEmpty(auditEnabled.auditTable(),
                auditTableNameProvider.getAuditTableName(baseTableName));
        auditParam.put("_auditTableName", auditTableName);
        EntityField[] ids = DTOClassInfo.getIdFields(clazz);
        Map<String, Object> pks = new HashMap<>();
        for (EntityField f : ids) {
            try {
                pks.put(DTOClassInfo.getColumnName(f), PropertyUtils.getProperty(dto, f.getName()));
            } catch (IllegalAccessException e) {
                //e.printStackTrace();
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        Map<String, Field> fieldsMap = new HashMap<>();
        ReflectionUtils.doWithFields(clazz, f -> fieldsMap.put(f.getName(), f));
        auditParam.put("_pks", pks);
        List<Map<String, Object>> res = auditMapper.selectAuditHistory(auditParam);
        List<Map<String, Object>> resTrans = new ArrayList<>();
        for (Map<String, Object> e : res) {
            Map<String, Object> n = new HashMap<>();
            e.forEach((k, v) -> {
                String camelName = AuditUtils.camel(k);
                if (v instanceof Date) {
                    Field f = fieldsMap.get(camelName);
                    v = AuditUtils.convertDateToString((Date) v, f);
                }
                n.put(camelName, v);
            });
            resTrans.add(n);
        }
        return resTrans;
    }

}
