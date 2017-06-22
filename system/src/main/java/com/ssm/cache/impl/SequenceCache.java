package com.ssm.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;

import com.ssm.fnd.dto.Sequence;
import com.ssm.fnd.mapper.SequenceMapper;

/**
 * @name SequenceCache
 * @description 序列号生成器缓存处理类
 * @author meixl
 */
public class SequenceCache extends HashStringRedisCache<Sequence> {

	private Logger logger = LoggerFactory.getLogger(SequenceCache.class);

	private String sequenceSql = SequenceMapper.class.getName() + ".selectAll";
	/**
	 * 调整序号调整范围
	 */
	public static final Long INCR_NUMBER = 100L;

	@Autowired
	private SequenceMapper sequenceMapper;

	@Override
	public void init() {
		setType(Sequence.class);
		strSerializer = getRedisTemplate().getStringSerializer();
		initLoad();
	}

	@Override
	public Sequence getValue(String key) {
		return super.getValue(key);
	}

	@Override
	public void setValue(String key, Sequence sequence) {
		super.setValue(key, sequence);
	}

	@Override
	public void remove(String key) {
		getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
			connection.hDel(strSerializer.serialize(getFullKey(key)), strSerializer.serialize(key));
			return null;
		});
	}

	/**
	 * 序列递增
	 * @param key
	 * @return
	 */
	public Long incr(String key) {
		return (Long) getRedisTemplate().execute((RedisCallback<Long>) (connection) -> {
            return connection.incr(key.getBytes());
        });
	}
	
	/**
	 * 序列删除
	 * @param key
	 */
	public void decr(String key) {
		getRedisTemplate().execute((RedisCallback<Long>) (connection) -> {
            return connection.decr(key.getBytes());
        });
	}

	public void reload(Long sequenceId) {
		try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
			Sequence sequence = sqlSession.selectOne(SequenceMapper.class.getName() + ".selectByPrimaryKey",
					sequenceId);
			setValue(sequence.getSequenceCode(), sequence);
		}
	}

	protected void initLoad() {
		Map<Long, Sequence> tempMap = new HashMap<>();
		try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
			sqlSession.select(sequenceSql, (resultContext) -> {
				Sequence sequence = (Sequence) resultContext.getResultObject();
				tempMap.put(sequence.getSequenceId(), sequence);
			});
			tempMap.forEach((k, v) -> {
			getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
				if(StringUtils.isNotBlank(v.getIncrCode())){
					String sequenceSeq = this.getFullKey(null)+":"+v.getIncrCode();
					boolean b = connection.exists(sequenceSeq.getBytes());
					if(!b){
						v.setSerialNumber((v.getSerialNumber() == null ? 0 : v.getSerialNumber()) + INCR_NUMBER);
						connection.set(sequenceSeq.getBytes(), v.getSerialNumber().toString().getBytes());
					    // 更新数据库当前序列
						sequenceMapper.updateByPrimaryKeySelective(v);
					}
				}
				return null;
			});
				setValue(v.getSequenceCode(), v);
			});
			tempMap.clear();
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("Init Sequence Cache Error:", e);
			}
		}
	}
}
