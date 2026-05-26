package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
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

    public void updateJobStatus(String deviceId, String response) {
        // Update the job status in the repository
        ProbeJobMessage job = probeJobRepository.findByDeviceId(deviceId);
        job.setStatus("SUCCESS");
        probeJobRepository.save(job);
    }

    public void retryFailedJobs(String batchId) {
        // Retry failed jobs in the batch
        // Implement circuit-breaker and exponential retry logic
    }
}