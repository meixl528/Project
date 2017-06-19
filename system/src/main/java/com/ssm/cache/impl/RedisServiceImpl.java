package com.ssm.cache.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.ssm.cache.IRedisService;

import redis.clients.jedis.Jedis;

@Service
public class RedisServiceImpl implements IRedisService{

	// 操作redis客户端
	private static Jedis jedis;
	@Autowired
	@Qualifier("jedisConnectionFactory")
	private JedisConnectionFactory jedisConnectionFactory;
	
	/**
	 * 获取一个jedis 客户端
	 * 
	 * @return
	 */
	private Jedis getJedis() {
		if (jedis == null) {
			return jedisConnectionFactory.getShardInfo().createResource();
		}
		return jedis;
	}
	

	/**
	 * 通过key删除（字节）
	 * 
	 * @param key
	 */
	public void del(byte[] key) {
		this.getJedis().del(key);
	}

	/**
	 * 通过key删除
	 * 
	 * @param key
	 */
	public void del(String key) {
		this.getJedis().del(key);
	}
	
	public void hdel(String key,String fields) {
		this.getJedis().hdel(key, fields);
	}
	
	public void hdel(String key) {
		Set<String> set = this.getJedis().hkeys(key);
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String k = it.next();
			this.getJedis().hdel(key, k);
		}
	}

	/**
	 * 添加key value 并且设置存活时间(byte)
	 * 
	 * @param key
	 * @param value
	 * @param liveTime
	 */
	public void set(byte[] key, byte[] value, int liveTime) {
		this.set(key, value);
		this.getJedis().expire(key, liveTime);
	}

	/**
	 * 添加key value 并且设置存活时间
	 * 
	 * @param key
	 * @param value
	 * @param liveTime
	 */
	public void set(String key, String value, int liveTime) {
		this.set(key, value);
		this.getJedis().expire(key, liveTime);
	}

	/**
	 * 添加key value
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		this.getJedis().set(key, value);
	}
	
	/**
	 * 添加key value (字节)(序列化)
	 * @param key
	 * @param value
	 */
	public void set(byte[] key, byte[] value) {
		this.getJedis().set(key, value);
	}
	
	public Set<String> hkeys(String key) {
		return this.getJedis().hkeys(key);
	}
	
	public void hset(String key,String field, String value) {
		this.getJedis().hset(key, field, value);
	}
	
	public String hget(String key,String field) {
		return this.getJedis().hget(key, field);
	}

	public void hmset(String key,Map<String,String> hash) {
		this.getJedis().hmset(key, hash);
	}
	
	public Map<String,String> hmget(String key) {
		Map<String,String> map = new HashMap<>();
		//List<String> list=this.getJedis().hmget(key,"name","pass","telephone","address");
		Set<String> set = this.getJedis().hkeys(key);
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String k = it.next();
			String v = this.getJedis().hget(key, k);
			map.put(k,v);
		}
		return map;
	}

	/**
	 * 获取redis value (String)
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String value = this.getJedis().get(key);
		return value;
	}

	/**
	 * 获取redis value (byte [] )(反序列化)
	 * 
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key) {
		return this.getJedis().get(key);
	}

	/**
	 * 存入List
	 * @param key
	 * @param strings
	 * @return
	 */
	public Long lpush(byte[] key,byte[]... strings) {
		return this.getJedis().lpush(key, strings);
	}
	/**
	 * 获取List
	 * @param key
	 * @return
	 */
	public List<byte[]> hvals(byte[] key) {
		return this.getJedis().hvals(key);
	}
	
	/**
	 * 获取Map
	 * @param key
	 * @return
	 */
	public Map<byte[],byte[]> hgetAll(byte[] key) {
		return this.getJedis().hgetAll(key);
	}
	/**
	 * 通过正则匹配keys
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern) {
		return this.getJedis().keys(pattern);
	}

	/**
	 * 检查key是否已经存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		return this.getJedis().exists(key);
	}
	public boolean exists(byte[] key) {
		return this.getJedis().exists(key);
	}
	

	/**
	 * 清空redis 当前db数据
	 * @return
	 */
	public String flushDB() {
		return this.getJedis().flushDB();
	}
	
	/**
	 * 清空redis 所有db数据
	 * @return
	 */
	public String flushAll() {
		return this.getJedis().flushAll();
	}

	/**
	 * 查看redis里有多少数据
	 */
	public long dbSize() {
		return this.getJedis().dbSize();
	}
	
	/**
	 * 选择redis db
	 * @param db
	 */
	public String select(int db){
		return this.getJedis().select(db);
	}

	/**
	 * 检查是否连接成功
	 * @return
	 */
	public String ping() {
		return this.getJedis().ping();
	}

}
