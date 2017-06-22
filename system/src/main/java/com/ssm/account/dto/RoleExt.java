package com.ssm.account.dto;

/**
 * @author meixl
 */
public class RoleExt extends Role {
    /**
	 */
	private static final long serialVersionUID = 1L;

	private Long surId;

    private Long userId;

    public Long getSurId() {
        return surId;
    }

    public void setSurId(Long surId) {
        this.surId = surId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}