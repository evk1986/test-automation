package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EnrichConsumerHandler {
    private static final Logger log = LoggerFactory.getLogger(EnrichConsumerHandler.class);
    private final EnrichConsumerService service;

    public EnrichConsumerHandler(EnrichConsumerService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Add Idempotency Key and Visibility Timeout Extension to Data-Enricher SQS Consum — received payload");
        service.execute();
    }
}
