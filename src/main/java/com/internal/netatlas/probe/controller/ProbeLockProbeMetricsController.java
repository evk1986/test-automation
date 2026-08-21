package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProbeLockProbeMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/probe/locks-metrics")
@Tag(name = "Probe Lock Metrics")
public class ProbeLockProbeMetricsController {

    private final ProbeLockProbeMetricsService probeLockProbeMetricsService;

    public ProbeLockProbeMetricsController(ProbeLockProbeMetricsService probeLockProbeMetricsService) {
        this.probeLockProbeMetricsService = probeLockProbeMetricsService;
    }

    @GetMapping("/{deviceId}/lock-status")
    @Operation(summary = "Check distributed lock status for a specific device")
    public ResponseEntity<Map<String, Object>> getLockStatus(@PathVariable String deviceId) {
        boolean isLocked = probeLockProbeMetricsService.isDeviceLocked(deviceId);
        return ResponseEntity.ok(Map.of(
            "deviceId", deviceId,
            "locked", isLocked,
            "status", isLocked ? "IN_PROGRESS" : "AVAILABLE"
        ));
    }

    @PostMapping("/{deviceId}/simulate-walk")
    @Operation(summary = "Simulate concurrent SNMP walk execution with distributed lock validation")
    public ResponseEntity<Map<String, Object>> simulateWalk(@PathVariable String deviceId) {
        boolean success = probeLockProbeMetricsService.executeWithLockAndMetric(deviceId);
        return ResponseEntity.ok(Map.of(
            "deviceId", deviceId,
            "executedSuccessfully", success,
            "ticket", "PRB-4821"
        ));
    }
}
