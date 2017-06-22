/*
 * #{copyright}#
 */

package com.ssm.audit.service.impl;

import com.ssm.audit.service.IAuditTableNameProvider;

/**
 * default impl, add '_a' suffix to baseTableName.
 *
 */
public class DefaultAuditTableNameProvider implements IAuditTableNameProvider {

    public static DefaultAuditTableNameProvider instance = new DefaultAuditTableNameProvider();

    private DefaultAuditTableNameProvider() {
    }

    @Override
    public String getAuditTableName(String baseTableName) {
        return baseTableName + "_a";
    }
}
