package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HazelcastLockImplementationMicrometerCounter {

    private static final Logger logger = LoggerFactory.getLogger(HazelcastLockImplementationMicrometerCounter.class);
    private final HazelcastInstance hazelcastInstance;
    private final Counter protocolFailureCounter;

    public HazelcastLockImplementationMicrometerCounter(HazelcastInstance hazelcastInstance,
                                                        MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.protocolFailureCounter = Counter.builder("probe.protocol.failure")
                .description("Count of protocol failures during SNMP walks")
                .tag("protocol", "SNMP")
                .register(meterRegistry);
    }

    public void performSnmpWalk(String deviceId, String oid) {
        String lockName = "snmp-walk-" + deviceId;
        CPSubsystem cpSubsystem = hazelcastInstance.getCPSubsystem();
        FencedLock lock = cpSubsystem.getLock(lockName);
        boolean acquired = false;
        try {
            acquired = lock.tryLock();
            if (!acquired) {
                logger.warn("SNMP walk lock contention for device {}", deviceId);
                protocolFailureCounter.increment();
                throw new IllegalStateException("Unable to acquire lock for SNMP walk");
            }
            // Simulated SNMP walk – replace with real SNMP adapter call
            logger.info("Executing SNMP walk for device {} on OID {}", deviceId, oid);
            // ... SNMP walk logic ...
        } catch (Exception e) {
            logger.error("SNMP walk failed for device {}: {}", deviceId, e.getMessage());
            protocolFailureCounter.increment();
            throw e;
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}
