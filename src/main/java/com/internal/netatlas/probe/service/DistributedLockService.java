package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class DistributedLockService {
    private final HazelcastInstance hazelcastInstance;

    public DistributedLockService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    /**
     * Attempts to acquire a distributed lock for the given key.
     *
     * @param lockKey the unique lock identifier
     * @return true if the lock was obtained, false otherwise
     */
    public boolean acquireLock(String lockKey) {
        ILock lock = hazelcastInstance.getLock(lockKey);
        try {
            // Try to acquire the lock within 5 seconds; adjust as needed for the pipeline timing.
            return lock.tryLock(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
