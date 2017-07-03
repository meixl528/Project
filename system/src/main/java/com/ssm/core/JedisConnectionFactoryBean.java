package com.ssm.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @name        JedisConnectionFactoryBean
 * @description redis 集群
 * @author      meixl
 * @date        2017年7月3日下午12:00:22
 * @version
 */
public class JedisConnectionFactoryBean implements FactoryBean<JedisConnectionFactory> {

    private boolean useSentinel = false;

    private RedisSentinelConfiguration sentinelConfiguration;

    private String hostName;

    private int port;

    private int database = 0;

    private String password;

    private JedisPoolConfig poolConfig;

    private volatile JedisConnectionFactory cacheObject;

    @Override
    public JedisConnectionFactory getObject() throws Exception {
        if (cacheObject == null) {
            synchronized (this) {
                if (cacheObject == null) {
                    doCreate();
                }
            }
        }
        return cacheObject;
    }

    private void doCreate() {
        if (useSentinel) {
            cacheObject = new JedisConnectionFactory(sentinelConfiguration);
        } else {
            cacheObject = new JedisConnectionFactory();
            cacheObject.setHostName(hostName);
            cacheObject.setPort(port);
            if(StringUtils.isNotEmpty(password)) {
                cacheObject.setPassword(password);
            }
        }
        cacheObject.setDatabase(database);
        cacheObject.setUsePool(true);
        cacheObject.setPoolConfig(poolConfig);
        cacheObject.afterPropertiesSet();
    }

    @Override
    public Class<?> getObjectType() {
        return JedisConnectionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public boolean isUseSentinel() {
        return useSentinel;
    }

    public void setUseSentinel(boolean useSentinel) {
        this.useSentinel = useSentinel;
    }

    public RedisSentinelConfiguration getSentinelConfiguration() {
        return sentinelConfiguration;
    }

    public void setSentinelConfiguration(RedisSentinelConfiguration sentinelConfiguration) {
        this.sentinelConfiguration = sentinelConfiguration;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JedisPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(JedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }
}