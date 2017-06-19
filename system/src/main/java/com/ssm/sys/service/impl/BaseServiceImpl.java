package com.ssm.sys.service.impl;

import java.util.List;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.core.request.IRequest;
import com.ssm.mybatis.common.Mapper;
import com.ssm.sys.dto.BaseDTO;
import com.ssm.sys.dto.DTOStatus;
import com.ssm.sys.service.IBaseService;

@Service
public abstract class BaseServiceImpl<T> implements IBaseService<T> {
	@Autowired
    protected Mapper<T> mapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> select(IRequest request, T condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return mapper.select(condition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T insert(IRequest request, T record) {
        mapper.insert(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T insertSelective(IRequest request, T record) {
        mapper.insertSelective(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T updateByPrimaryKey(IRequest request, T record) {
        mapper.updateByPrimaryKey(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public T updateByPrimaryKeySelective(IRequest request,T record) {
        mapper.updateByPrimaryKeySelective(record);
        return record;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public T selectByPrimaryKey(IRequest request, T record) {
        return mapper.selectByPrimaryKey(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(T record) {
        return mapper.deleteByPrimaryKey(record);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> selectAll() {
        return mapper.selectAll();
    }
    
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<T> selectAll(IRequest request) {
        return mapper.selectAll();
    }

    /**
     * this method assume the object in list is BaseDTO.
     * 
     * @param request
     *            requestContext
     * @param list
     *            dto list
     * @return the list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<T> batchUpdate(IRequest request, List<T> list) {
        IBaseService<T> self = ((IBaseService<T>) AopContext.currentProxy());
        for (T t : list) {
            switch (((BaseDTO) t).get__status()) {
            case DTOStatus.ADD:
                self.insertSelective(request, t);
                break;
            case DTOStatus.UPDATE:
                self.updateByPrimaryKeySelective(request, t);
                break;
            case DTOStatus.DELETE:
                self.deleteByPrimaryKey(t);
                break;
            default:
                break;
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<T> list) {
        IBaseService<T> self = ((IBaseService<T>) AopContext.currentProxy());
        int c = 0;
        for (T t : list) {
            c += self.deleteByPrimaryKey(t);
        }
        return c;
    }
}