package com/internal/netatlas/probe/service;

import com.internal/netatlas/probe.model.ProbeJob;
import com.internal/netatlas/probe.repository.ProbeJobRepository;
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
        // Retrieve failed probe jobs from Cassandra
        Iterable<ProbeJob> failedJobs = probeJobRepository.findByBatchIdAndStatus(batchId, "FAILED");
        // Retry failed jobs
        for (ProbeJob job : failedJobs) {
            // Update job status to PENDING
            job.setStatus("PENDING");
            probeJobRepository.save(job);
        }
    }
}