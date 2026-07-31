package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.service.ProtocolMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/probe/metrics")
public class ProtocolMetricsController {

    private final ProtocolMetricsService metricsService;

    public ProtocolMetricsController(ProtocolMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/protocol-failure")
    public ResponseEntity<Map<String, Integer>> getProtocolFailureMetrics() {
        Map<String, Integer> metrics = metricsService.fetchFailureMetrics();
        return ResponseEntity.ok(metrics);
    }
}
