package com.ssm.account.service;

public interface IRole {
    Long getRoleId();

    String getRoleCode();

    String getRoleName();

    boolean isEnabled();

    boolean isActive();
}