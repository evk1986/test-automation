package com.internal.netatlas.orchestrate.config;

import org.springframework.stereotype.Service;

@Service
public class FleetOrchestratorBatchConfigService {
    public void updateBatchConfig(int rapidPollQueueDepthThreshold) {
        // Update batch config with rapid-poll queue depth threshold
        System.out.println("Updated batch config with rapid-poll queue depth threshold: " + rapidPollQueueDepthThreshold);
    }
}