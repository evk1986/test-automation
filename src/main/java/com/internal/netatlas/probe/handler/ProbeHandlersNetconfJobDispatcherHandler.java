package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProbeHandlersNetconfJobDispatcherHandler {
    private static final Logger log = LoggerFactory.getLogger(ProbeHandlersNetconfJobDispatcherHandler.class);
    private final ProbeHandlersNetconfJobDispatcherService service;

    public ProbeHandlersNetconfJobDispatcherHandler(ProbeHandlersNetconfJobDispatcherService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Implement NETCONF subtree handler for Cisco IOS-XR NCS devices (PRB-4821) — received payload");
        service.execute();
    }
}
