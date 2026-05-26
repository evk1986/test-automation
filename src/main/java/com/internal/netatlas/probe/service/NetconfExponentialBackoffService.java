package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NetconfExponentialBackoffService {

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfExponentialBackoffService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryWithExponentialBackoff(ProbeJobMessage message) {
        int retryCount = 0;
        long initialDelay = 1000; // 1 second
        long maxDelay = 30000; // 30 seconds
        long backoffMultiplier = 2;

        while (retryCount < 5) {
            try {
                // Simulate NETCONF session timeout
                if (Math.random() < 0.5) {
                    throw new RuntimeException("NETCONF session timeout");
                }
                // Process message
                System.out.println("Processed message: " + message);
                break;
            } catch (Exception e) {
                retryCount++;
                long delay = Math.min(initialDelay * (long) Math.pow(backoffMultiplier, retryCount), maxDelay);
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (retryCount >= 5) {
            // Route to dead-letter queue
            System.out.println("Routing to dead-letter queue");
        }
    }
}