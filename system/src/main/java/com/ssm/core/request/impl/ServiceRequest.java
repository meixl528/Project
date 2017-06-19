/*
 * #{copyright}#
 */

package com.ssm.core.request.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssm.core.request.IRequest;

/**
 * 默认的 IRequest 实现.
 * 
 * @author meixl
 */
public class ServiceRequest implements IRequest {

    private static final String ATTR_ACCOUNT_ID = "_accountId";

    private static final String ATTR_ROLE_ID = "_roleId";

    private static final String ATTR_COMPANY_ID = "_companyId";

    private static final String ATTR_LOCALE = "_locale";

    private Long accountId = -1L;
    private Long roleId = -1L;
    private Long companyId = -1L;
    private String locale;
    @JsonIgnore
    private Map<String, Object> attributeMap = new HashMap<>();

    @Override
    public Long getUserId() {
        return accountId;
    }

    @Override
    public void setUserId(Long userId) {
        this.accountId = userId;
        setAttribute(ATTR_ACCOUNT_ID, userId);
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
        setAttribute(ATTR_LOCALE, locale);
    }

    @Override
    public Long getRoleId() {
        return roleId;
    }

    @Override
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
        setAttribute(ATTR_ROLE_ID, roleId);
    }

    @Override
    public Long getCompanyId() {
        return companyId;
    }

    @Override
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
        setAttribute(ATTR_COMPANY_ID, companyId);
    }

    @Override
    @SuppressWarnings("unchecked")
    @JsonAnyGetter
    public <T> T getAttribute(String name) {
        return (T) attributeMap.get(name);
    }

    @Override
    @JsonAnySetter
    public void setAttribute(String name, Object value) {
        attributeMap.put(name, value);
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }

    @Override
    @JsonIgnore
    public Set<String> getAttributeNames() {
        return attributeMap.keySet();
    }
}
