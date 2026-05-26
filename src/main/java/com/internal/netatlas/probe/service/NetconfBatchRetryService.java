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

    public void retryFailedJobs(String batchId) {
        // Implement exponential-backoff retry logic
        // For demonstration purposes, a simple retry mechanism is shown
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                // Simulate a successful retry
                System.out.println("Retry successful");
                break;
            } catch (Exception e) {
                retryCount++;
                // Simulate an exponential backoff
                try {
                    Thread.sleep(1000 * (long) Math.pow(2, retryCount));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}