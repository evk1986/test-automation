package com.internal.netatlas.probe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class HazelcastDistributedLockForSnmpEventListener {
    private static final Logger log = LoggerFactory.getLogger(HazelcastDistributedLockForSnmpEventListener.class);
    private final HazelcastDistributedLockForSnmpService service;

    public HazelcastDistributedLockForSnmpEventListener(HazelcastDistributedLockForSnmpService service) {
        this.service = service;
    }

    @EventListener
    public void onReady(ApplicationReadyEvent event) {
        // Add Hazelcast distributed lock for SNMP walks and SQS idempotency in Device-Prob — post-startup hook
        log.info("HazelcastDistributedLockForSnmp event listener initialized — validating service availability");
        service.execute();
    }
}
