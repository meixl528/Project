package com.ssm.lock.impl;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ssm.lock.DistributedLockCallback;
import com.ssm.lock.DistributedLockProvider;
import com.ssm.lock.exception.LockException;
import com.ssm.lock.util.LockKeyUtil;

/**
 * Multiple Instance mode 分布式锁模板
 */
public class MultipleDistributedLockProvider implements DistributedLockProvider {

    private static final Logger logger = LoggerFactory.getLogger(MultipleDistributedLockProvider.class);
    private static final long DEFAULT_TIMEOUT = 60 * 10;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    private static final long DEFAULT_WAIT_TIME = 1;

    private List<RedissonClient> redissons;

    public MultipleDistributedLockProvider() {
    }

    public MultipleDistributedLockProvider(List<RedissonClient> redissons) {
        this.redissons = redissons;
    }

    @Override
    public <T> T lock(String lockKey, DistributedLockCallback<T> callback) {
        return lock(lockKey, callback, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T lock(Object lockKey, DistributedLockCallback<T> callback) {
        return lock(LockKeyUtil.getLockKey(lockKey), callback, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T lock(Object lockKey, DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit) {
        return lock(LockKeyUtil.getLockKey(lockKey), callback, leaseTime, timeUnit);
    }

    @Override
    public <T> T lock(String lockKey, DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit) {
        RedissonRedLock lock = null;
        try {
            lock = new RedissonRedLock(getRlocks(lockKey));
            lock.lock(leaseTime, timeUnit);
            return callback.process();
        } catch (Exception e) {
            logger.error("callback  process error", e);
            throw new RuntimeException(e);
        } finally {
            if (lock != null) {
            	logger.info(lockKey + " unlock!");
                lock.unlock();
            }
        }
    }
    
    @Override
    public <T> T tryLock(String lockKey, DistributedLockCallback<T> callback) throws Exception {
        return tryLock(lockKey, callback, DEFAULT_WAIT_TIME, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T tryLock(Object lockKey, DistributedLockCallback<T> callback) throws Exception {
        return tryLock(LockKeyUtil.getLockKey(lockKey), callback, DEFAULT_WAIT_TIME, DEFAULT_TIMEOUT,
                DEFAULT_TIME_UNIT);
    }

    @Override
    public <T> T tryLock(Object lockKey, DistributedLockCallback<T> callback, long waitTime, long leaseTime,
            TimeUnit timeUnit) throws Exception {
        return tryLock(LockKeyUtil.getLockKey(lockKey), callback, waitTime, leaseTime, timeUnit);
    }

    @Override
    public <T> T tryLock(String lockKey, DistributedLockCallback<T> callback, long waitTime, long leaseTime,
            TimeUnit timeUnit) throws Exception {
        RedissonRedLock lock = null;
        try {
            lock = new RedissonRedLock(getRlocks(lockKey));
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                return callback.process();
            } else {
                throw new LockException(LockException.ERROR_GET_LOCKKEY_FAILURE,null);
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    public void setRedisson(List<RedissonClient> redissons) {
        this.redissons = redissons;
    }

    private RLock[] getRlocks(String lockKey) {
        RLock[] rLock = new RLock[redissons.size()];
        for (int i = 0; i < redissons.size(); i++) {
            RedissonClient redisson = redissons.get(i);
            rLock[i] = redisson.getLock(lockKey);
        }
        return rLock;
    }
    
    /**
     * 根据锁关键字释放锁
     * @param lockKey
     */
    @Override
    public void releaseLock(String lockKey){ 
    	RedissonRedLock lock = new RedissonRedLock(getRlocks(lockKey));
    	if (lock != null) {
            lock.unlock();
        }
    	
    	/*RLock rLock = getRlocks(lockKey)[0];
    	if (rLock != null && rLock.isExists() && rLock.isLocked()) {
    		rLock.unlock();
        }*/
    }
    
    /*
     * 自定义
     */
    @Override
    public boolean lock(String lockKey){
    	return lock(lockKey,DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT);
    }
    
    /*
     * 自定义
     */
    @Override
    public boolean lock(String lockKey, long releaseTime, TimeUnit timeUnit){
		RLock lock = null;
        try {
        	RLock[] rLock = getRlocks(lockKey);
        	if(rLock.length == 1){
        		lock = rLock[0];
                if(lock.isExists() && lock.isLocked()){
                	return false;
                }
                lock.lock(releaseTime, timeUnit);
        	}
        	return true;
        } catch (Exception e) {
            logger.error("callback  process error", e);
            throw new RuntimeException(e);
        }
	}

}