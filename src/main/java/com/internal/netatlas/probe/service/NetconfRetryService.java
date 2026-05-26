package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NetconfRetryService {

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retry(ProbeJob probeJob, Exception e) {
        int retryCount = probeJob.getAttemptCount();
        if (retryCount < 3) {
            probeJob.setAttemptCount(retryCount + 1);
            probeJobRepository.save(probeJob);
            try {
                TimeUnit.SECONDS.sleep((long) Math.pow(2, retryCount));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // retry the operation
        } else {
            // route to dead-letter queue
            probeJob.setStatus("DLQ");
            probeJobRepository.save(probeJob);
        }
    }
}