package com.internal.netatlas.orchestrate.controller;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import com.internal.netatlas.orchestrate.service.FleetOrchestratorBatchConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FleetOrchestratorBatchConfigController {
    @Autowired
    private FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService;

    @GetMapping("/api/v1/orchestrator/batch-config")
    public BatchConfig getBatchConfig() {
        BatchConfig batchConfig = new BatchConfig();
        fleetOrchestratorBatchConfigService.updateBatchConfig(batchConfig);
        return batchConfig;
    }
}