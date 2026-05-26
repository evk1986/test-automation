package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class NetconfExponentialBackoffService {

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfExponentialBackoffService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryWithExponentialBackoff(ProbeJobMessage message) {
        int maxAttempts = 5;
        int attempt = 0;
        Random random = new Random();
        while (attempt < maxAttempts) {
            try {
                // Simulate a NETCONF session
                System.out.println("NETCONF session established");
                break;
            } catch (Exception e) {
                attempt++;
                long backoff = (long) (Math.pow(2, attempt) * 1000 + random.nextInt(1000));
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (attempt == maxAttempts) {
            // Route to dead-letter queue
            System.out.println("Routing to dead-letter queue");
        }
    }
}