package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class NetconfRetryService {

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryNetconfSession(ProbeJobMessage message) {
        // Implement exponential backoff retry strategy
        int attemptCount = 0;
        Duration initialDelay = Duration.ofSeconds(1);
        Duration maxDelay = Duration.ofMinutes(5);
        while (attemptCount < 5) {
            try {
                // Simulate NETCONF session timeout handling
                TimeUnit.SECONDS.sleep(1);
                break;
            } catch (Exception e) {
                attemptCount++;
                Duration delay = initialDelay.multipliedBy((long) Math.pow(2, attemptCount)).min(maxDelay);
                try {
                    TimeUnit.SECONDS.sleep(delay.getSeconds());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (attemptCount >= 5) {
            // Update probe job status in Cassandra
            probeJobRepository.updateProbeJobStatus(message.getJobId(), "FAILED");
        }
    }
}