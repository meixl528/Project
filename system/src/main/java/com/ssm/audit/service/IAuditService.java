/*
 * #{copyright}#
 */
package com.ssm.audit.service;


import java.util.List;

import com.ssm.audit.dto.Audit;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
/**
 * 审计service.
 *
 */
public interface IAuditService extends ProxySelf<IAuditService> {

    /**
     * 查询审计开关信息.
     *
     * @param requestContext
     *            统一上下文
     * @return list 审计开关list
     */
    List<Audit> selectAuditEntityAll(IRequest requestContext);

    /**
     * 保存审计开关信息.
     *
     * @param requestContext
     *            统一上下文
     * @return list 审计开关list
     */
    boolean saveAuditEntityAll(IRequest requestContext, List<Audit> audits);

    /**
     * 更新审计开关信息.
     *
     * @param audit
     *            新数据
     * @return 成功与否
     */
    Audit updateAudit(Audit audit);


}
