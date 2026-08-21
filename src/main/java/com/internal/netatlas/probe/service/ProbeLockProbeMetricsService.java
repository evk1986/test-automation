package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Service
public class ProbeLockProbeMetricsService {

    private final HazelcastInstance hazelcastInstance;
    private final MeterRegistry meterRegistry;

    public ProbeLockProbeMetricsService(HazelcastInstance hazelcastInstance, MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.meterRegistry = meterRegistry;
    }

    public boolean executeWithLock(String batchId, String deviceId, String protocol, String region, Runnable task) {
        String lockKey = "probe:lock:" + batchId + ":" + deviceId;
        Lock lock = hazelcastInstance.getCPSubsystem().getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(5, TimeUnit.SECONDS);
            if (!acquired) {
                recordProtocolFailure(protocol, region, "LOCK_TIMEOUT");
                return false;
            }
            task.run();
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            recordProtocolFailure(protocol, region, "INTERRUPTED");
            return false;
        } catch (Exception e) {
            recordProtocolFailure(protocol, region, "EXECUTION_FAILED");
            return false;
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }

    public void recordProtocolFailure(String protocol, String region, String reason) {
        Counter.builder("probe.protocol.failures")
                .tag("protocol", protocol != null ? protocol : "UNKNOWN")
                .tag("region", region != null ? region : "UNKNOWN")
                .tag("reason", reason != null ? reason : "UNKNOWN")
                .register(meterRegistry)
                .increment();
    }
}
