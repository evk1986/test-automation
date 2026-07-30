package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ComTelecomProbeMetricsHandler {
    private static final Logger log = LoggerFactory.getLogger(ComTelecomProbeMetricsHandler.class);
    private final ComTelecomProbeMetricsService service;

    public ComTelecomProbeMetricsHandler(ComTelecomProbeMetricsService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Add Micrometer counters for protocol failures and DLQ size gauge (PRB-4821) — received payload");
        service.execute();
    }
}
