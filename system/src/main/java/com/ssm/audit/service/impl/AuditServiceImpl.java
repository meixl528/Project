/*
 * #{copyright}#
 */
package com.ssm.audit.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.audit.dto.Audit;
import com.ssm.audit.mapper.AuditMapper;
import com.ssm.audit.service.IAuditService;
import com.ssm.cache.annotation.CacheSet;
import com.ssm.core.request.IRequest;

/**
 * 审计serviceImpl.
 *
 */
@Transactional
@Service
public class AuditServiceImpl implements IAuditService {

    @Autowired
    private AuditMapper auditMapper;

    @Override
    public List<Audit> selectAuditEntityAll(IRequest requestContext) {
        return auditMapper.selectAuditEntityAll();
    }

    @Override
    public boolean saveAuditEntityAll(IRequest requestContext, List<Audit> audits) {
        for (Audit audit : audits) {
            self().updateAudit(audit);
        }
        return true;
    }

    @Override
    @CacheSet(cache = "audit")
    public Audit updateAudit(Audit audit) {
        auditMapper.saveAuditEntity(audit);
        return audit;
    }


}
