/*
 * Copyright Hand China Co.,Ltd.
 */

package com.ssm.extensible.dto;

/**
 * @author shengyang.zhou@hand-china.com
 */
public interface IBaseDto {
    String __ID = "__id";
    String __STATUS = "__status";
    String __TLS = "__tls";
    String SORTNAME = "sortname";
    String SORTORDER = "sortorder";

    String _TOKEN = "_token";

    String REQUEST_ID = "requestId";
    String PROGRAM_ID = "programId";
    String OBJECT_VERSION_NUMBER = "objectVersionNumber";
    String CREATED_BY = "createdBy";
    String CREATION_DATE = "creationDate";
    String LAST_UPDATED_BY = "lastUpdatedBy";
    String LAST_UPDATE_DATE = "lastUpdateDate";
    String LAST_UPDATE_LOGIN = "lastUpdateLogin";

    Object getAttribute(String key);

    void setAttribute(String key,Object value);
}
