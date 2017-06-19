package com.ssm.sys.service;

import java.util.List;

import com.ssm.core.annotation.StdWho;
import com.ssm.core.proxy.ProxySelf;
import com.ssm.core.request.IRequest;
import com.ssm.sys.dto.Code;
import com.ssm.sys.dto.CodeValue;

/**
 * 描素维护
 * @author meixl
 */
public interface ICodeService extends ProxySelf<ICodeService> {

    List<Code> selectCodes(IRequest request, Code code, int page, int pagesize);

    List<CodeValue> selectCodeValues(IRequest request, CodeValue value);

    Code createCode(Code code);

    boolean batchDelete(IRequest request, List<Code> codes);

    boolean batchDeleteValues(IRequest request, List<CodeValue> values);

    Code updateCode(Code code);

    List<Code> batchUpdate(IRequest request, @StdWho List<Code> codes);
    
    /**
     * 根据code查询所有代码值.
     * 
     * @param request
     * @param code
     * @return 代码值
     */
    List<CodeValue> selectCodeValuesByCodeName(IRequest request, String codeName);
    
    
    /**
     * 根据代码和值获取CodeValue.
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
    CodeValue getCodeValue(IRequest request, String codeName, String value);

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
    String getCodeValueByMeaning(IRequest request, String codeName, String meaning);

    /**
     * 根据代码和值获取Meaning.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return meaning 含义
     */
    String getCodeMeaningByValue(IRequest request, String codeName, String value);

    /**
     * 根据代码和值获取描述.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return description 描述
     */
    String getCodeDescByValue(IRequest request, String codeName, String value);
}