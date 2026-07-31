package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProbeHandlersNetconfProbeLockingHandler {
    private static final Logger log = LoggerFactory.getLogger(ProbeHandlersNetconfProbeLockingHandler.class);
    private final ProbeHandlersNetconfProbeLockingService service;

    public ProbeHandlersNetconfProbeLockingHandler(ProbeHandlersNetconfProbeLockingService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Add Hazelcast distributed lock and idempotency for NETCONF probes (PRB-4821) — received payload");
        service.execute();
    }
}
