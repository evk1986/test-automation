package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetconfBatchRetryService {

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfBatchRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryFailedJobs(String batchId) {
        List<ProbeJob> failedJobs = probeJobRepository.findByBatchIdAndStatus(batchId, "FAILED");
        for (ProbeJob job : failedJobs) {
            // Release lock and retry the job
            job.setStatus("PENDING");
            probeJobRepository.save(job);
        }
    }
}