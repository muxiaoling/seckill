package com.tang.seckill.lock;

import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 */
public interface DistributedLocker {
    RLock lock(String lockKey);

    RLock lock(String lockKey, int timeout);

    RLock lock(String lockKey, TimeUnit unit, int timeout);

    boolean tryLock(String lockKey, TimeUnit unit, long leaseTime);

    boolean tryLock(String lockKey, TimeUnit unit, long waitTime, long leaseTime);

    void unlock(String lockKey);

    void unlock(RLock lock);
}
