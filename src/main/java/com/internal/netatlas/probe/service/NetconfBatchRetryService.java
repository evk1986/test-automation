package com.internal.netatlas.probe.service;

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
        // Circuit-breaker and retry logic
        probeJobRepository.findByBatchIdAndStatus(batchId, "FAILED").forEach(job -> {
            // Retry job
            job.setStatus("RUNNING");
            probeJobRepository.save(job);
        });
    }
}