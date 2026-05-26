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
        // circuit-breaker → retry → update ProbeJob status in Cassandra
        probeJobRepository.findByBatchIdAndStatus(batchId, "FAILED").forEach(probeJob -> {
            // retry logic
            probeJob.setStatus("RUNNING");
            probeJobRepository.save(probeJob);
        });
    }
}