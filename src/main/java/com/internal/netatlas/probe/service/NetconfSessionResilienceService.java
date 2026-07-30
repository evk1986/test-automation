package com.internal.netatlas.probe.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NetconfSessionResilienceService {
    private static final Logger log = LoggerFactory.getLogger(NetconfSessionResilienceService.class);

    public String execute() {
        log.info("Add circuit breaker & retry to NETCONF NCS handler (PRB-4821) — processing");
        // ## Description Introduce resilience to the NETCONF NCS handler by wrapping session creation with a Resilience4j circuit 
        return "TES-106: processing complete";
    }
}
