package com.tang.seckill.lock.impl;

import com.tang.seckill.lock.DistributedLocker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author muxiaoling
 * @date 2022/9/15 17:46
 */
@Component
public class DistributedLockerImpl implements DistributedLocker {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    @Override
    public RLock lock(String lockKey, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
        return lock;
    }

    @Override
    public RLock lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(0L, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    @Override
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }

    @Override
    public void unlock(RLock lock) {
        if (lock.isLocked()) {
            lock.unlock();
        }
    }
}
