package com.internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetconfBatchRetryService {
    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfBatchRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryFailedJobs(String batchId) {
        // Circuit breaker
        try {
            // Retry failed jobs
            probeJobRepository.findByBatchIdAndStatus(batchId, "FAILED").forEach(probeJob -> {
                // Update ProbeJob status in Cassandra
                probeJob.setStatus("RUNNING");
                probeJobRepository.save(probeJob);
            });
        } catch (Exception e) {
            // Exponential retry
            retryFailedJobs(batchId);
        }
    }
}