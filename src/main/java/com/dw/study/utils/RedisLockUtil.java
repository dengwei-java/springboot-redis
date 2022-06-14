package com.dw.study.utils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Author dw
 * @ClassName RedisLockUtil
 * @Description redisson 分布式锁工具类
 * @Date 2022/6/13 9:07
 * @Version 1.0
 */
@Component
public class RedisLockUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockUtil.class);

    /**
     * 默认锁过期时间
     */
    private static final Long EXPIRE_TIME = 60L;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 加锁
     *
     * @param lockKey
     * @return
     */
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 加锁，过期自动释放
     *
     * @param lockKey
     * @param leaseTime 自动释放锁时间
     * @return
     */
    public RLock lock(String lockKey, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 加锁，过期自动释放，时间单位传入
     *
     * @param lockKey
     * @param unit      时间单位
     * @param leaseTime 上锁后自动释放时间
     * @return
     */
    public RLock lock(String lockKey, TimeUnit unit, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, unit);
        return lock;
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey
     * @param unit      时间单位
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放时间
     * @return
     */
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            logger.info("尝试获取分布式锁： {}", lockKey);
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            logger.error("获取分布式锁失败！");
            return false;
        }
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @return
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            logger.info("尝试获取分布式锁： {}", lockKey);
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("获取分布式锁失败！");
            return false;
        }
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey
     * @param time    最多等待时间秒
     * @return
     */
    public boolean tryLock(String lockKey, long time) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            logger.info("尝试获取分布式锁： {}", lockKey);
            return lock.tryLock(time, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("获取分布式锁失败！");
            return false;
        }
    }

    /**
     * 尝试获取锁, 默认等待 60s
     *
     * @param lockKey
     * @return
     */
    public boolean tryLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            logger.info("尝试获取分布式锁： {}", lockKey);
            return lock.tryLock(EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("获取分布式锁失败: {}", lockKey);
            return false;
        }
    }

    /**
     * 释放锁
     *
     * @param lockKey
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 释放锁
     *
     * @param lock
     */
    public void unlock(RLock lock) {
        lock.unlock();
    }

}
