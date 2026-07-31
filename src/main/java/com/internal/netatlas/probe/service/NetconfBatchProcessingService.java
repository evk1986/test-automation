package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.ProbeJobStatus;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetconfBatchProcessingService {

    private final ProbeJobRepository probeJobRepository;
    private final HazelcastInstance hazelcastInstance;
    private final MeterRegistry meterRegistry;

    public NetconfBatchProcessingService(ProbeJobRepository probeJobRepository,
                                         HazelcastInstance hazelcastInstance,
                                         MeterRegistry meterRegistry) {
        this.probeJobRepository = probeJobRepository;
        this.hazelcastInstance = hazelcastInstance;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Processes all pending NETCONF jobs for a given batch. The method is idempotent – a distributed
     * Hazelcast lock guarantees that only one instance processes the batch at a time.
     */
    public void processBatch(String batchId) {
        String lockName = "netconf-batch-" + batchId;
        ILock lock = hazelcastInstance.getLock(lockName);
        if (!lock.tryLock()) {
            // Another pod is already handling this batch.
            return;
        }
        try {
            List<ProbeJob> pendingJobs = probeJobRepository
                    .findByBatchIdAndStatus(batchId, ProbeJobStatus.PENDING);
            for (ProbeJob job : pendingJobs) {
                boolean success = simulateNetconfCall(job);
                if (success) {
                    job.setStatus(ProbeJobStatus.SUCCESS);
                } else {
                    job.setStatus(ProbeJobStatus.FAILED);
                    job.setLastErrorMessage("NETCONF execution failed");
                }
                probeJobRepository.save(job);
            }
            // Emit a batch‑level metric for observability.
            meterRegistry.counter("netconf.batch.processed", "batchId", batchId).increment();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Minimal stub that pretends a NETCONF call always succeeds. In a real implementation this would
     * delegate to a NetconfAdapter that talks to the device.
     */
    private boolean simulateNetconfCall(ProbeJob job) {
        return true;
    }
}
