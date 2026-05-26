package com.internal.netatlas.probe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/probe/jobs")
@Tag(name = "HardenFleetOrchestratorBatchConfigA")
public class HardenFleetOrchestratorBatchConfigAController {
    private final HardenFleetOrchestratorBatchConfigAService service;

    public HardenFleetOrchestratorBatchConfigAController(HardenFleetOrchestratorBatchConfigAService service) {
        this.service = service;
    }

    @GetMapping("/harden-fleet-orchestrator-batch-config-a")
    @Operation(summary = "Harden Fleet-Orchestrator batch-config and cron overlap guard")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(service.execute());
    }
}
