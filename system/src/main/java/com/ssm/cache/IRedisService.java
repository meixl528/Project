package com.ssm.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * @name        IRedisService
 * @description 自定义redis
 * @author      meixl
 * @date        2017年6月12日上午10:10:11
 * @version
 */
public interface IRedisService {

	void del(byte[] key);
	void del(String key);
	void hdel(String key,String fields);
	void hdel(String key);

	Set<String> hkeys(String key);
	Set<String> keys(String pattern);
	
	void set(byte[] key, byte[] value, int liveTime);
	void set(String key, String value, int liveTime);
	void set(String key, String value);
	void set(byte[] key, byte[] value);
	void hset(String key,String field, String value);
	void hmset(String key,Map<String,String> hash);
	Map<String,String> hmget(String key);
	
	String hget(String key,String field);
	String get(String key);
	byte[] get(byte[] key);
	
	Long lpush(byte[] key,byte[]... strings);
	List<byte[]> hvals(byte[] key);
	Map<byte[],byte[]> hgetAll(byte[] key);
	
	boolean exists(String key);
	boolean exists(byte[] key);
	String flushDB();
	String flushAll();
	String select(int db);
	long dbSize();
	String ping();
	
}
