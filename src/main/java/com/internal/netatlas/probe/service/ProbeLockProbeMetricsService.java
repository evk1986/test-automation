package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ProbeLockProbeMetricsService {

    private static final Logger log = LoggerFactory.getLogger(ProbeLockProbeMetricsService.class);
    private static final long LOCK_WAIT_TIMEOUT_SECONDS = 5;

    private final HazelcastInstance hazelcastInstance;
    private final MeterRegistry meterRegistry;
    private final ProbeJobRepository probeJobRepository;

    public ProbeLockProbeMetricsService(HazelcastInstance hazelcastInstance,
                                        MeterRegistry meterRegistry,
                                        ProbeJobRepository probeJobRepository) {
        this.hazelcastInstance = hazelcastInstance;
        this.meterRegistry = meterRegistry;
        this.probeJobRepository = probeJobRepository;
    }

    public boolean processProbeWithLock(ProbeJob job) {
        String lockKey = "probe-lock-" + job.getDeviceId();
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(lockKey);

        boolean acquired = false;
        try {
            acquired = lock.tryLock(LOCK_WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!acquired) {
                log.warn("Could not acquire Hazelcast lock for deviceId {} within timeout. Skipping execution.", job.getDeviceId());
                recordLockAcquisitionFailure(job.getProtocol());
                return false;
            }

            log.info("Acquired Hazelcast lock for deviceId: {}, running probe job: {}", job.getDeviceId(), job.getId());
            job.setStatus("RUNNING");
            job.setAttemptCount(job.getAttemptCount() + 1);
            probeJobRepository.save(job);

            executeDeviceProbe(job);

            job.setStatus("SUCCESS");
            probeJobRepository.save(job);
            recordProbeSuccess(job.getProtocol());
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Probe lock acquisition interrupted for device: {}", job.getDeviceId(), e);
            recordProbeFailure(job.getProtocol(), "LOCK_INTERRUPTED");
            return false;
        } catch (Exception e) {
            log.error("Error executing probe for device: {}, batch: {}", job.getDeviceId(), job.getBatchId(), e);
            job.setStatus("FAILED");
            job.setLastErrorMessage(e.getMessage());
            probeJobRepository.save(job);
            recordProbeFailure(job.getProtocol(), e.getClass().getSimpleName());
            return false;
        } finally {
            if (acquired && lock.isLockedByCurrentThread()) {
                lock.unlock();
                log.info("Released Hazelcast lock for deviceId: {}", job.getDeviceId());
            }
        }
    }

    private void executeDeviceProbe(ProbeJob job) {
        if ("OFFLINE".equalsIgnoreCase(job.getDeviceId())) {
            throw new IllegalStateException("Device unreachable via protocol " + job.getProtocol());
        }
    }

    private void recordProbeSuccess(String protocol) {
        Counter.builder("probe.protocol.execution.success")
                .tag("protocol", protocol != null ? protocol : "UNKNOWN")
                .description("Count of successful device probe executions")
                .register(meterRegistry)
                .increment();
    }

    private void recordProbeFailure(String protocol, String reason) {
        Counter.builder("probe.protocol.execution.failure")
                .tag("protocol", protocol != null ? protocol : "UNKNOWN")
                .tag("reason", reason)
                .description("Count of failed device probe executions")
                .register(meterRegistry)
                .increment();
    }

    private void recordLockAcquisitionFailure(String protocol) {
        Counter.builder("probe.lock.acquisition.failure")
                .tag("protocol", protocol != null ? protocol : "UNKNOWN")
                .description("Count of failed Hazelcast lock acquisitions due to concurrency")
                .register(meterRegistry)
                .increment();
    }
}
