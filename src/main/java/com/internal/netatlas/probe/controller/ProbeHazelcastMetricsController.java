package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProbeHazelcastMetricsService;
import com.internal.netatlas.probe.service.ProbeHazelcastMetricsService.ProtocolFailureMetricDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/probe/hazelcast")
public class ProbeHazelcastMetricsController {

    private final ProbeHazelcastMetricsService metricsService;

    public ProbeHazelcastMetricsController(ProbeHazelcastMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics/protocol-failures")
    public ResponseEntity<ProtocolFailureMetricDto> getProtocolFailureMetrics() {
        ProtocolFailureMetricDto dto = metricsService.getProtocolFailureMetrics();
        return ResponseEntity.ok(dto);
    }
}
