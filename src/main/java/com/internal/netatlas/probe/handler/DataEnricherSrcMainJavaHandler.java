package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataEnricherSrcMainJavaHandler {
    private static final Logger log = LoggerFactory.getLogger(DataEnricherSrcMainJavaHandler.class);
    private final DataEnricherSrcMainJavaService service;

    public DataEnricherSrcMainJavaHandler(DataEnricherSrcMainJavaService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Implement idempotency and visibility timeout handling in Data-Enricher SQS consu — received payload");
        service.execute();
    }
}
