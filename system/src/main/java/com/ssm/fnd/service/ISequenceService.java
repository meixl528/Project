package com.ssm.fnd.service;

import com.ssm.core.proxy.ProxySelf;
import com.ssm.fnd.dto.Sequence;
import com.ssm.sys.service.IBaseService;

/**
 * @name ISequenceService
 * @description 序列号规则配置
 * @author meixl
 */
public interface ISequenceService extends IBaseService<Sequence>, ProxySelf<ISequenceService>{
	
	/**
	 * 创建序列
	 * @param sequence
	 * @return
	 */
	Sequence createSequence(Sequence sequence);
	
	/**
	 * 更新序列
	 * @param sequence
	 * @return
	 */
	Sequence updateSequence(Sequence sequence);
	
	/**
	 * 获取序号
	 * @param sequenceCode 序列代码
	 * @return
	 */
	public String getSequence(String sequenceCode);
	
	/**
	 * 获取序号
	 * @param sequenceCode 序列代码
	 * @param sequencePrefix 序列传入前缀
	 * @return
	 */
	public String getSequence(String sequenceCode,String sequencePrefix);

	/**
	 * 重新加载数据到Redis
	 */
	public void refreshSequenceForRedis();

}