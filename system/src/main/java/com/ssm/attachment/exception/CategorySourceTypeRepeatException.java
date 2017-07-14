package com.ssm.attachment.exception;

import com.ssm.core.exception.BaseException;

/**
 */
public class CategorySourceTypeRepeatException extends BaseException {

    private static final long serialVersionUID = 9046687211507280533L;
    
    /**
     * 创建附件分类时，sourceType重复错误.
     */
    private static final String ATTACH_CATEGORY_REPEAT = "msg.warning.dto.attachcategory.sourcetype.repeaterror";

    public CategorySourceTypeRepeatException() {
        super(ATTACH_CATEGORY_REPEAT, ATTACH_CATEGORY_REPEAT, new Object[0]);
    }
}
