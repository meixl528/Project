package com.ssm.generator.service;

import com.ssm.generator.dto.GeneratorInfo;

import java.util.List;

/**
 */
public interface IGeneratorService {
    public List<String> showTables();

    public int generatorFile(GeneratorInfo info);

}
