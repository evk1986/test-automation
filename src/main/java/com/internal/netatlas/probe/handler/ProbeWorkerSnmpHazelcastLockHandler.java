package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProbeWorkerSnmpHazelcastLockHandler {
    private static final Logger log = LoggerFactory.getLogger(ProbeWorkerSnmpHazelcastLockHandler.class);
    private final ProbeWorkerSnmpHazelcastLockService service;

    public ProbeWorkerSnmpHazelcastLockHandler(ProbeWorkerSnmpHazelcastLockService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Add Hazelcast lock & visibility timeout for SNMP walks in Device-Probe (PRB-5001 — received payload");
        service.execute();
    }
}
