package com.ssm.sys.service.impl;

import org.springframework.stereotype.Service;

import com.ssm.cache.annotation.CacheDelete;
import com.ssm.cache.annotation.CacheSet;
import com.ssm.core.request.IRequest;
import com.ssm.sys.dto.Prompt;
import com.ssm.sys.service.IPromptService;

/**
 * @author meixl
 */
@Service
public class PromptServiceImpl extends BaseServiceImpl<Prompt> implements IPromptService {

    @Override
    @CacheSet(cache = "prompt")
    public Prompt insertSelective(IRequest request, Prompt prompt) {
        super.insertSelective(request, prompt);
        return prompt;
    }

    @Override
    @CacheDelete(cache = "prompt")
    public int deleteByPrimaryKey(Prompt prompt) {
        return super.deleteByPrimaryKey(prompt);
    }

    @Override
    @CacheSet(cache = "prompt")
    public Prompt updateByPrimaryKeySelective(IRequest request, Prompt prompt) {
        super.updateByPrimaryKeySelective(request, prompt);
        return prompt;
    }
}