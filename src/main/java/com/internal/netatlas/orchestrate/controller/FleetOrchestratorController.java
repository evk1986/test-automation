package com.internal.netatlas.orchestrate.controller;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import com.internal.netatlas.orchestrate.service.FleetOrchestratorBatchConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orchestrate")
public class FleetOrchestratorController {

    private final FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService;

    @Autowired
    public FleetOrchestratorController(FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService) {
        this.fleetOrchestratorBatchConfigService = fleetOrchestratorBatchConfigService;
    }

    @PutMapping("/batch-config")
    public ResponseEntity<BatchConfig> updateBatchConfig(@RequestBody BatchConfig batchConfig) {
        fleetOrchestratorBatchConfigService.updateBatchConfig(batchConfig);
        return ResponseEntity.ok(batchConfig);
    }
}