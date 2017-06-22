/*
 * #{copyright}#
 */

package com.ssm.audit.service;

/**
 * translate baseTableName to auditTableName.
 *
 */
public interface IAuditTableNameProvider {

    /**
     *
     * @param baseTableName
     *            base table name
     * @return audit table name
     */
    String getAuditTableName(String baseTableName);
}
