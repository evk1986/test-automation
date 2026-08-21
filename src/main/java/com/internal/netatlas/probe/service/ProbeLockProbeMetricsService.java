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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service;
public class ProbeLockProbeMetricsService {

    private static final Logger log = LoggerFactory.getLogger(ProbeLockProbeMetricsService.class);
    private static final String LOCK_PREFIX = "lock:probe:device:";

    private final HazelcastInstance hazelcastInstance;
    private final MeterRegistry meterRegistry;
    private final ProbeLockProbeMetricsRepository repository;

    public ProbeLockProbeMetricsService(HazelcastInstance hazelcastInstance,
                                        MeterRegistry meterRegistry,
                                        ProbeLockProbeMetricsRepository repository) {
        this.hazelcastInstance = hazelcastInstance;
        this.meterRegistry = meterRegistry;
        this.repository = repository;
    }

    public boolean executeWithLockAndMetrics(ProbeJob job, Runnable executionTask) {
        String lockKey = LOCK_PREFIX + job.getDeviceId();
        FencedLock lock = hazelcastInstance.getCPSubsystem().getFencedLock(lockKey);

        boolean acquired = false;
        try {
            acquired = lock.tryLock(5, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("Could not acquire Hazelcast lock for deviceId={}, jobId={}", job.getDeviceId(), job.getId());
                recordFailureMetric(job.getProtocol(), "LOCK_ACQUISITION_TIMEOUT");
                job.setStatus("FAILED");
                job.setLastErrorMessage("Hazelcast lock acquisition timed out for device " + job.getDeviceId());
                repository.save(job);
                return false;
            }

            log.info("Acquired Hazelcast lock for deviceId={}, executing probe jobId={}", job.getDeviceId(), job.getId());
            job.setStatus("RUNNING");
            repository.save(job);

            executionTask.run();

            job.setStatus("SUCCESS");
            repository.save(job);
            return true;
        } catch (Exception ex) {
            log.error("Execution failed for jobId={} on deviceId={}", job.getId(), job.getDeviceId(), ex);
            recordFailureMetric(job.getProtocol(), ex.getClass().getSimpleName());
            job.setStatus("FAILED");
            job.setLastErrorMessage(ex.getMessage());
            repository.save(job);
            return false;
        } finally {
            if (acquired && lock.isLockedByCurrentThread()) {
                lock.unlock();
                log.info("Released Hazelcast lock for deviceId={}", job.getDeviceId());
            }
        }
    }

    public void recordFailureMetric(String protocol, String reason) {
        Counter.builder("probe.protocol.failure.count")
                .description("Counts protocol collection failures during probe executions")
                .tag("protocol", protocol != null ? protocol : "UNKNOWN")
                .tag("reason", reason != null ? reason : "UNKNOWN")
                .register(meterRegistry)
                .increment();
    }

    public Optional<ProbeJob> findJobById(String id) {
        return repository.findById(id);
    }
}