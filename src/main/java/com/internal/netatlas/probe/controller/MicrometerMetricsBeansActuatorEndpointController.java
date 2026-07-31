package com.internal.netatlas.probe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/probe/jobs")
@Tag(name = "MicrometerMetricsBeansActuatorEndpoint")
public class MicrometerMetricsBeansActuatorEndpointController {
    private final MicrometerMetricsBeansActuatorEndpointService service;

    public MicrometerMetricsBeansActuatorEndpointController(MicrometerMetricsBeansActuatorEndpointService service) {
        this.service = service;
    }

    @GetMapping("/add-micrometer-failure-rate-metrics-for-")
    @Operation(summary = "Add Micrometer failure‑rate metrics for Device‑Probe protocols")
    public ResponseEntity<String> handle() {
        return ResponseEntity.ok(service.execute());
    }
}
