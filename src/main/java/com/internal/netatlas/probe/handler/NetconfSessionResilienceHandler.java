package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NetconfSessionResilienceHandler {
    private static final Logger log = LoggerFactory.getLogger(NetconfSessionResilienceHandler.class);
    private final NetconfSessionResilienceService service;

    public NetconfSessionResilienceHandler(NetconfSessionResilienceService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Add circuit breaker & retry to NETCONF NCS handler (PRB-4821) — received payload");
        service.execute();
    }
}
