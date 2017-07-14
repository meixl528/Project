package com.ssm.account.dto;

/**
 * @author meixl
 */
public class RoleExt extends Role {
    /**
	 */
	private static final long serialVersionUID = 1L;

	private Long urId;

    private Long userId;
 
    public Long getUrId() {
		return urId;
	}

	public void setUrId(Long urId) {
		this.urId = urId;
	}

	public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}