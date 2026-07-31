package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service that retries failed NETCONF probe jobs and records protocol‑specific failure metrics.
 */
@Service
public class NetconfBatchRetryService {

    private final ProbeJobRepository probeJobRepository;
    private final MeterRegistry meterRegistry;

    public NetconfBatchRetryService(ProbeJobRepository probeJobRepository, MeterRegistry meterRegistry) {
        this.probeJobRepository = probeJobRepository;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Retries all failed NETCONF jobs belonging to the supplied batch.
     * For every job that still fails after the retry attempt a Micrometer counter is incremented
     * with tags {@code protocol=NETCONF} and {@code region=<job region>}.
     */
    @Transactional
    public void retryFailedJobs(String batchId) {
        List<ProbeJob> failedJobs = probeJobRepository.findByBatchIdAndStatus(batchId, ProbeJob.Status.FAILED);
        for (ProbeJob job : failedJobs) {
            try {
                // Placeholder for actual NETCONF retry logic (circuit‑breaker, session handling, etc.)
                // Assume retryJob(job) throws an exception when the retry fails.
                retryJob(job);
                job.setStatus(ProbeJob.Status.SUCCESS);
            } catch (Exception ex) {
                job.setStatus(ProbeJob.Status.FAILED);
                job.setLastErrorMessage(ex.getMessage());
                // Record a failure metric for this NETCONF attempt.
                recordFailure(job.getRegion());
            }
            probeJobRepository.save(job);
        }
    }

    private void retryJob(ProbeJob job) throws Exception {
        // Minimal stub – in real code this would invoke the NETCONF adapter.
        // For illustration we simulate a failure when the attempt count is odd.
        if (job.getAttemptCount() % 2 == 1) {
            throw new Exception("Simulated NETCONF retry failure");
        }
        // Simulate successful retry.
        job.setAttemptCount(job.getAttemptCount() + 1);
    }

    /**
     * Increments the {@code probe.protocol.failures} counter for NETCONF failures.
     *
     * @param region the AWS region where the device resides (e.g. "us-east-1")
     */
    public void recordFailure(String region) {
        Counter.builder("probe.protocol.failures")
                .tag("protocol", "NETCONF")
                .tag("region", region)
                .register(meterRegistry)
                .increment();
    }
}
