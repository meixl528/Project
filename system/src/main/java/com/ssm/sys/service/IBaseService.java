package com.ssm.sys.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.ssm.core.request.IRequest;

public interface IBaseService<T> {

    List<T> select(IRequest request, T condition, int pageNum, int pageSize);

    T insert(IRequest request, T record);

    T insertSelective(IRequest request, T record);

    T updateByPrimaryKey(IRequest request, T record);

    @Transactional(rollbackFor = Exception.class)
    T updateByPrimaryKeySelective(IRequest request, T record);

    T selectByPrimaryKey(IRequest request, T record);

    int deleteByPrimaryKey(T record);

    /**
     * DO NOT USE this method when multi-language query
     * @return
     */
    @Deprecated
    List<T> selectAll();

    List<T> selectAll(IRequest iRequest);

    List<T> batchUpdate(IRequest request, List<T> list);

    int batchDelete(List<T> list);
}
