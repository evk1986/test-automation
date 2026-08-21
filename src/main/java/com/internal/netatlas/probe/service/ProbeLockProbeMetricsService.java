package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeLockProbeMetricsRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProbeLockProbeMetricsService {

    private static final Logger log = LoggerFactory.getLogger(ProbeLockProbeMetricsService.class);

    private final HazelcastInstance hazelcastInstance;
    private final MeterRegistry meterRegistry;
    private final ProbeLockProbeMetricsRepository repository;
    private final Counter failureCounter;

    public ProbeLockProbeMetricsService(HazelcastInstance hazelcastInstance,
                                       MeterRegistry meterRegistry,
                                       ProbeLockProbeMetricsRepository repository) {
        this.hazelcastInstance = hazelcastInstance;
        this.meterRegistry = meterRegistry;
        this.repository = repository;
        this.failureCounter = Counter.builder("probe.protocol.failures")
            .description("Count of protocol execution failures during probe operations")
            .tag("service", "device-probe")
            .register(meterRegistry);
    }

    public boolean isDeviceLocked(String deviceId) {
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock("device-lock-" + deviceId);
        return lock.isLocked();
    }

    public boolean executeWithLockAndMetric(String deviceId) {
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock("device-lock-" + deviceId);
        if (!lock.tryLock()) {
            log.warn("Device lock contention detected for deviceId: {}. Skipping duplicate walk.", deviceId);
            return false;
        }

        try {
            log.info("Acquired Hazelcast lock for deviceId: {}", deviceId);
            ProbeJob job = new ProbeJob();
            job.setId(UUID.randomUUID().toString());
            job.setDeviceId(deviceId);
            job.setProtocol("SNMP");
            job.setRegion("prod-use1");
            job.setBatchId("BATCH-PRB-20240523-USE1-01");
            job.setStatus("SUCCESS");
            job.setAttemptCount(1);

            repository.save(job);
            return true;
        } catch (Exception ex) {
            log.error("Protocol error during SNMP walk for deviceId: {}", deviceId, ex);
            failureCounter.increment();
            return false;
        } finally {
            if (lock.isLockedByCurrentThread()) {
                lock.unlock();
                log.info("Released Hazelcast lock for deviceId: {}", deviceId);
            }
        }
    }
}
