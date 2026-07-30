package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.NetconfSubtreeJobRepository;
import org.springframework.stereotype.Service;

@Service
public class NetconfSubtreeService {

    private static final int DEFAULT_TIMEOUT_MS = 10_000; // increased from 5_000 to avoid timeout

    private final NetconfSubtreeJobRepository repository;

    public NetconfSubtreeService(NetconfSubtreeJobRepository repository) {
        this.repository = repository;
    }

    public void processSubtreeJob(ProbeJobMessage message) {
        // Simulate NETCONF subtree retrieval with timeout handling
        try {
            // In a real implementation, invoke NETCONF adapter with timeout
            Thread.sleep(Math.min(DEFAULT_TIMEOUT_MS, 100)); // placeholder for network call
            // Persist a placeholder job record
            repository.saveJob(message.getJobId(), message.getDeviceId(), "SUCCESS");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            repository.saveJob(message.getJobId(), message.getDeviceId(), "FAILED");
        }
    }
}
