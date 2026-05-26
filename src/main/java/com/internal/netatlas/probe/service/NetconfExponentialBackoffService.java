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
    private final Random random = new Random();

    @Autowired
    public NetconfExponentialBackoffService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryWithExponentialBackoff(ProbeJobMessage message) {
        int attempt = 0;
        Duration initialDelay = Duration.ofSeconds(1);
        Duration maxDelay = Duration.ofMinutes(5);
        double backoffMultiplier = 2;

        while (attempt < 5) {
            try {
                // Simulate a NETCONF session timeout
                if (random.nextDouble() < 0.5) {
                    throw new RuntimeException("NETCONF session timeout");
                }
                // Process the message
                System.out.println("Processed message: " + message);
                break;
            } catch (RuntimeException e) {
                attempt++;
                Duration delay = initialDelay.multipliedBy((long) Math.pow(backoffMultiplier, attempt)).plus(Duration.ofSeconds(random.nextInt(1000)));
                delay = delay.compareTo(maxDelay) > 0 ? maxDelay : delay;
                System.out.println("Retrying in " + delay + "...");
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (attempt >= 5) {
            // Route to dead-letter queue
            System.out.println("Routing to dead-letter queue...");
        }
    }
}