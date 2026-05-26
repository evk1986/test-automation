package com.internal.netatlas.orchestrate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FleetOrchestratorBatchConfigController {
    @Autowired
    private FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService;

    @PutMapping("/api/v1/orchestrate/config")
    public void updateBatchConfig(@RequestBody FleetOrchestratorBatchConfig batchConfig) {
        fleetOrchestratorBatchConfigService.updateBatchConfig(batchConfig.getRapidPollQueueDepthThreshold());
    }
}