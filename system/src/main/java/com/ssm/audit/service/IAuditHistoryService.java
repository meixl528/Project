/*
 * #{copyright}#
 */

package com.ssm.audit.service;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.ssm.sys.dto.BaseDTO;

/**
 */
public interface IAuditHistoryService {

    /**
     * get audit history(s) of given record.
     * <p>
     * the primary key(s) of dto must not be null
     *
     * @param dto
     *            query parameter , contains pks
     * @param page
     *            pageNum
     * @param pagesize
     *            pageSize
     * @return list of histories, ordered by audit_id desc
     */
    List<Map<String, Object>> selectAuditHistory(BaseDTO dto, int page, int pagesize) throws InvocationTargetException, NoSuchMethodException;
}
