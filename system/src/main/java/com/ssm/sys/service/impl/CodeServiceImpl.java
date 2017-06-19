package com.ssm.sys.service.impl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.ssm.cache.impl.SysCodeCache;
import com.ssm.core.ILanguageProvider;
import com.ssm.core.request.IRequest;
import com.ssm.sys.dto.Code;
import com.ssm.sys.dto.CodeValue;
import com.ssm.sys.dto.Language;
import com.ssm.sys.mapper.CodeMapper;
import com.ssm.sys.mapper.CodeValueMapper;
import com.ssm.sys.service.ICodeService;

/**
 * @author meixl
 */
@Service
@Transactional
public class CodeServiceImpl implements ICodeService {
    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private CodeValueMapper codeValueMapper;

    @Autowired
    private ILanguageProvider languageProvider;

    @Autowired
    private SysCodeCache codeCache;

    /**
     * 批量操作快码行数据.
     *
     * @param code
     *            头行数据
     */
    private void processCodeValues(Code code) {
        for (CodeValue codeValue : code.getCodeValues()) {
            if (codeValue.getCodeValueId() == null) {
                codeValue.setCodeId(code.getCodeId()); // 设置头ID跟行ID一致
                codeValueMapper.insertSelective(codeValue);
            } else if (codeValue.getCodeValueId() != null) {
                codeValueMapper.updateByPrimaryKeySelective(codeValue);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Code> selectCodes(IRequest request, Code code, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<Code> codes = codeMapper.select(code);
        return codes;
    }

    @Override
    public List<CodeValue> selectCodeValues(IRequest request, CodeValue value) {
        return codeValueMapper.selectCodeValuesByCodeId(value);
    }

    @Override
    public List<CodeValue> selectCodeValuesByCodeName(IRequest request, String codeName) {
        Code code = codeCache.getValue(codeName + "." + request.getLocale());
        if (code != null) {
            return code.getCodeValues();
        } else {
            return codeValueMapper.selectCodeValuesByCodeName(codeName);
        }
    }

    /**
     * 根据代码和值获取CodeValue.
     * <p>
     * 从 cache 直接取值.
     *
     * @param request
     *            请求上下文
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return codeValue 代码值DTO
     * @author frank.li
     */
    @Override
    public CodeValue getCodeValue(IRequest request, String codeName, String value) {
        Code code = codeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (value.equals(v.getValue())) {
                return v;
            }
        }
        return null;
    };

    /**
     * 根据代码和含义获取代码值.
     * <p>
     * 从 cache 直接取值.
     *
     * @param request
     *            请求上下文
     * @param codeName
     *            代码
     * @param meaning
     *            含义
     * @return value 代码值
     * @author frank.li
     */
    @Override
    public String getCodeValueByMeaning(IRequest request, String codeName, String meaning) {
        Code code = codeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (meaning.equals(v.getMeaning())) {
                return v.getValue();
            }
        }
        return null;
    };

    /**
     * 根据代码和值获取含义.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return meaning 含义
     */
    @Override
    public String getCodeMeaningByValue(IRequest request, String codeName, String value) {
        Code code = codeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (value.equals(v.getValue())) {
                return v.getMeaning();
            }
        }
        return null;
    };

    /**
     * 根据代码和值获取描述.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return description 描述
     */
    @Override
    public String getCodeDescByValue(IRequest request, String codeName, String value) {
        Code code = codeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getCodeValues() == null) {
            return null;
        }
        for (CodeValue v : code.getCodeValues()) {
            if (value.equals(v.getValue())) {
                return v.getDescription();
            }
        }
        return null;
    };

    @Override
    public Code createCode(Code code) {
        // 插入头行
        codeMapper.insertSelective(code);
        // 判断如果行不为空，则迭代循环插入
        if (code.getCodeValues() != null) {
            processCodeValues(code);
        }
        for (Language lang : languageProvider.getSupportedLanguages()) {
            codeCache.setValue(code.getCode() + "." + lang.getLangCode(), code);
        }
        return code;
    }

    @Override
    public boolean batchDelete(IRequest request, List<Code> codes) {
        // 删除头行
        for (Code code : codes) {
            CodeValue codeValue = new CodeValue();
            codeValue.setCodeId(code.getCodeId());
            // 首先删除行的多语言数据
            codeValueMapper.deleteTlByCodeId(codeValue);
            // 然后删除行
            codeValueMapper.deleteByCodeId(codeValue);

            // 最后删除头
            codeMapper.deleteByPrimaryKey(code);
            codeCache.remove(code.getCode());
        }
        return true;
    }

    @Override
    public boolean batchDeleteValues(IRequest request, List<CodeValue> values) {
        Set<Long> codeIdSet = new HashSet<>();
        for (CodeValue value : values) {
            codeValueMapper.deleteByPrimaryKey(value);
            codeIdSet.add(value.getCodeId());
        }
        for (Long codeId : codeIdSet) {
            codeCache.reload(codeId);
        }

        return true;
    }

    @Override
    public Code updateCode(Code code) {
        codeMapper.updateByPrimaryKeySelective(code);
        // 判断如果行不为空，则迭代循环插入
        if (code.getCodeValues() != null) {
            processCodeValues(code);
        }
        codeCache.remove(code.getCode());
        codeCache.reload(code.getCodeId());
        return code;
    }

    @Override
    public List<Code> batchUpdate(IRequest request, List<Code> codes) {
        for (Code code : codes) {
            if (code.getCodeId() == null) {
                self().createCode(code);
            } else if (code.getCodeId() != null) {
                self().updateCode(code);
            }
        }
        return codes;
    }

}