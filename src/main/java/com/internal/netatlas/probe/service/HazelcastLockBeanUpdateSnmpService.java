package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service that executes an SNMP walk under a Hazelcast distributed lock.
 * The lock key follows the schema "deviceId|batchId".
 */
@Service
public class HazelcastLockBeanUpdateSnmpService {

    private static final Logger LOG = LoggerFactory.getLogger(HazelcastLockBeanUpdateSnmpService.class);
    private static final long LOCK_TIMEOUT_SECONDS = 5L;

    private final HazelcastInstance hazelcastInstance;
    private final Counter lockAcquiredCounter;
    private final Counter lockReleasedCounter;

    public HazelcastLockBeanUpdateSnmpService(HazelcastInstance hazelcastInstance, MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.lockAcquiredCounter = Counter.builder("snmp.lock.acquired")
                .description("Number of successful SNMP lock acquisitions")
                .register(meterRegistry);
        this.lockReleasedCounter = Counter.builder("snmp.lock.released")
                .description("Number of SNMP lock releases")
                .register(meterRegistry);
    }

    /**
     * Executes the SNMP walk for the supplied job message while holding a distributed lock.
     *
     * @param message the incoming probe job message
     */
    public void executeSnmpWalk(ProbeJobMessage message) {
        String lockKey = message.getDeviceId() + "|" + message.getBatchId();
        ILock lock = hazelcastInstance.getLock(lockKey);
        boolean acquired = false;
        try {
            LOG.debug("Attempting to acquire lock for key {}", lockKey);
            acquired = lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                LOG.warn("Could not acquire lock for device {} in batch {} within {} seconds",
                        message.getDeviceId(), message.getBatchId(), LOCK_TIMEOUT_SECONDS);
                // In a real implementation we would update job status to FAILED or DLQ.
                return;
            }
            lockAcquiredCounter.increment();
            LOG.info("Lock acquired for device {} in batch {}. Starting SNMP walk.",
                    message.getDeviceId(), message.getBatchId());

            // -----------------------------------------------------------------
            // Placeholder for the actual SNMP walk execution logic.
            // In production this would invoke the SNMP protocol adapter, collect
            // raw data and publish the result to the next pipeline stage.
            // -----------------------------------------------------------------
            simulateSnmpWalk();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Lock acquisition thread was interrupted for device {}", message.getDeviceId(), e);
        } finally {
            if (acquired && lock.isLockedByCurrentThread()) {
                lock.unlock();
                lockReleasedCounter.increment();
                LOG.info("Lock released for device {} in batch {}", message.getDeviceId(), message.getBatchId());
            }
        }
    }

    private void simulateSnmpWalk() {
        try {
            // Simulate a short processing delay; replace with real SNMP logic.
            Thread.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
