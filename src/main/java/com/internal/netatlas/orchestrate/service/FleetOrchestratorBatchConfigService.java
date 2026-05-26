package com.internal.netatlas.orchestrate.service;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import org.springframework.stereotype.Service;

@Service
public class FleetOrchestratorBatchConfigService {
    public void updateBatchConfig(BatchConfig batchConfig) {
        // Update batch-config with rapid-poll queue depth threshold
        batchConfig.setRapidPollQueueDepthThreshold(1000);
        // Implement cron overlap guard
        batchConfig.setCronOverlapGuard(true);
    }
}