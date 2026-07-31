package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.cp.lock.FencedLock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

/**
 * Provides a Hazelcast distributed lock for SNMP walks and a Micrometer counter for
 * per‑protocol failure metrics.
 */
@Service
public class HazelcastLockImplementationMicrometerCounter {

    private static final Logger LOG = LoggerFactory.getLogger(HazelcastLockImplementationMicrometerCounter.class);
    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(30);

    private final HazelcastInstance hazelcastInstance;
    private final Counter failureCounter;

    public HazelcastLockImplementationMicrometerCounter(HazelcastInstance hazelcastInstance,
                                                         MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.failureCounter = Counter.builder("probe.protocol.failures")
                .description("Number of protocol failures during device probing")
                .tags("service", "device-probe")
                .register(meterRegistry);
    }

    /**
     * Acquires a distributed lock for the given device and batch.
     *
     * @return true if the lock was obtained, false otherwise.
     */
    public boolean acquireLock(String deviceId, String batchId) {
        String lockName = lockKey(deviceId, batchId);
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(lockName);
        try {
            boolean acquired = lock.tryLock(LOCK_TIMEOUT.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
            if (acquired) {
                LOG.debug("Acquired lock {}", lockName);
            }
            return acquired;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Interrupted while trying to acquire lock {}", lockName, e);
            return false;
        }
    }

    /**
     * Releases the lock for the given device and batch.
     */
    public void releaseLock(String deviceId, String batchId) {
        String lockName = lockKey(deviceId, batchId);
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(lockName);
        if (lock.isLockedByCurrentThread()) {
            lock.unlock();
            LOG.debug("Released lock {}", lockName);
        } else {
            LOG.warn("Attempted to release lock {} which is not owned by current thread", lockName);
        }
    }

    private String lockKey(String deviceId, String batchId) {
        return "snmp-walk-lock-" + batchId + "-" + deviceId;
    }

    /**
     * Records a failure for the given protocol and region.
     */
    public void recordFailure(String protocol, String region) {
        // Create a new counter with tags for each distinct combination; Micrometer merges them.
        Counter.builder("probe.protocol.failures")
                .description("Number of protocol failures during device probing")
                .tags("protocol", protocol, "region", region)
                .register(failureCounter.getId().getRegistry())
                .increment();
        LOG.info("Incremented failure metric for protocol={} region={}", protocol, region);
    }

    /**
     * Simulates an SNMP walk. Returns false to indicate a failure for demonstration purposes.
     * In production this would call the real SNMP adapter.
     */
    public boolean performSnmpWalkSimulation(ProbeJobMessage message) {
        // Simple deterministic failure simulation: every 5th device fails.
        int hash = Math.abs(message.getDeviceId().hashCode());
        return (hash % 5) != 0;
    }
}
