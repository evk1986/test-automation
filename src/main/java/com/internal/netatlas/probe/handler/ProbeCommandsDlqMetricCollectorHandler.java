package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProbeCommandsDlqMetricCollectorHandler {
    private static final Logger log = LoggerFactory.getLogger(ProbeCommandsDlqMetricCollectorHandler.class);
    private final ProbeCommandsDlqMetricCollectorService service;

    public ProbeCommandsDlqMetricCollectorHandler(ProbeCommandsDlqMetricCollectorService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Add DLQ visibility metrics and automated drain endpoint for probe.commands (ORCH — received payload");
        service.execute();
    }
}
