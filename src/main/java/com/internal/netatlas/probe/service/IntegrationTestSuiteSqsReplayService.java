package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IntegrationTestSuiteSqsReplayService {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestSuiteSqsReplayService.class);
    private final HazelcastInstance hazelcastInstance;
    private final Counter failureCounter;

    public IntegrationTestSuiteSqsReplayService(HazelcastInstance hazelcastInstance, MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.failureCounter = Counter.builder("probe.protocol.failures")
                .description("Number of protocol failures during probe")
                .tag("protocol", "SNMP")
                .register(meterRegistry);
    }

    public void process(ProbeJobMessage message) {
        String lockName = "snmp-walk-lock-" + message.getDeviceId();
        ILock lock = hazelcastInstance.getLock(lockName);
        boolean acquired = false;
        try {
            acquired = lock.tryLock();
            if (!acquired) {
                logger.warn("Another SNMP walk is already in progress for device {}", message.getDeviceId());
                return;
            }
            logger.info("Acquired lock for device {}", message.getDeviceId());
            // Simulated SNMP walk – test harness can set injectFailure flag
            if (Boolean.TRUE.equals(message.getInjectFailure())) {
                logger.error("Injected SNMP failure for device {}", message.getDeviceId());
                failureCounter.increment();
                // In production we would route to DLQ here
            } else {
                logger.info("SNMP walk succeeded for device {}", message.getDeviceId());
            }
        } finally {
            if (acquired) {
                lock.unlock();
                logger.info("Released lock for device {}", message.getDeviceId());
            }
        }
    }
}
