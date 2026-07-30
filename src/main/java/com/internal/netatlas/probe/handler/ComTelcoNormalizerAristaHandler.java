package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ComTelcoNormalizerAristaHandler {
    private static final Logger log = LoggerFactory.getLogger(ComTelcoNormalizerAristaHandler.class);
    private final ComTelcoNormalizerAristaService service;

    public ComTelcoNormalizerAristaHandler(ComTelcoNormalizerAristaService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Implement Schema-Normalizer mapper for Arista EOS eAPI \"show interfaces\" (NORM-5 — received payload");
        service.execute();
    }
}
