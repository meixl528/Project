/*
 * #{copyright}#
 */

package com.ssm.audit.mapper;


import java.util.List;
import java.util.Map;

import com.ssm.audit.dto.Audit;

/**
 */
public interface AuditMapper {

    int auditInsert(Map map);

    List<Map<String, Object>> selectAuditHistory(Map<String, Object> para);

    List<Audit> selectAuditEntityAll();

    int saveAuditEntity(Audit audit);

}

