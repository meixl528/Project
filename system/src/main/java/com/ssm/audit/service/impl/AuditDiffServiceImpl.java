/*
 * #{copyright}#
 */

package com.ssm.audit.service.impl;

import com.github.pagehelper.PageHelper;

/**
 */
public class AuditDiffServiceImpl {
    public void getAuditHistory(String tableName, Long id, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

    }
}
