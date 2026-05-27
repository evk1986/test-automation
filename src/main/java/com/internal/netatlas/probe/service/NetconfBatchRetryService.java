package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NetconfBatchRetryService {
    private static final Logger LOGGER = Logger.getLogger(NetconfBatchRetryService.class.getName());

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfBatchRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void updateJobStatus(String jobId, String rawResponse) {
        // Update the job status in the database
        ProbeJob job = probeJobRepository.findById(jobId).orElseThrow();
        job.setStatus("SUCCESS");
        job.setRawResponse(rawResponse);
        probeJobRepository.save(job);
    }
}