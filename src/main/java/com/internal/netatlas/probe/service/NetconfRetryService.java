package com.internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetconfRetryService {
    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryFailedJobs(String batchId) {
        // circuit-breaker → retry → update ProbeJob status in Cassandra
        probeJobRepository.findByBatchId(batchId).forEach(probeJob -> {
            if (probeJob.getStatus().equals("FAILED")) {
                // retry logic here
                probeJob.setStatus("RUNNING");
                probeJobRepository.save(probeJob);
            }
        });
    }
}