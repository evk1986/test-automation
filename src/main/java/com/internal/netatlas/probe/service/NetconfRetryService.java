package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class NetconfRetryService {
    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retry(ProbeJobMessage message) {
        AtomicInteger attemptCount = new AtomicInteger(0);
        Duration initialBackoff = Duration.ofSeconds(1);
        Duration maxBackoff = Duration.ofMinutes(10);
        Duration backoff = initialBackoff;

        while (attemptCount.get() < 5) {
            try {
                // Simulate NETCONF session timeout
                Thread.sleep(backoff.toMillis());
                // Retry logic here
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            attemptCount.incrementAndGet();
            backoff = Duration.ofSeconds((long) Math.pow(2, attemptCount.get()));
            if (backoff.compareTo(maxBackoff) > 0) {
                backoff = maxBackoff;
            }
        }
        if (attemptCount.get() >= 5) {
            // Route to dead-letter queue
            probeJobRepository.updateStatus(message.getJobId(), "DLQ");
        }
    }
}