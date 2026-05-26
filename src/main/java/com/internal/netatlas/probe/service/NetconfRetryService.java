package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class NetconfRetryService {

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retry(ProbeJob probeJob) {
        int retryCount = probeJob.getAttemptCount();
        long delay = (long) Math.pow(2, retryCount) * 1000;
        // Add some randomness to the delay
        delay += new Random().nextInt(1000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Update the probe job status and save it to the repository
        probeJob.setStatus("RUNNING");
        probeJobRepository.save(probeJob);
    }
}