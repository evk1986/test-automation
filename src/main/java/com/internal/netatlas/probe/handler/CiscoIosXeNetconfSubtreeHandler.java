package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CiscoIosXeNetconfSubtreeHandler {
    private static final Logger log = LoggerFactory.getLogger(CiscoIosXeNetconfSubtreeHandler.class);
    private final CiscoIosXeNetconfSubtreeService service;

    public CiscoIosXeNetconfSubtreeHandler(CiscoIosXeNetconfSubtreeService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("Implement Cisco IOS-XE NETCONF Subtree Strategy in Device-Probe (PRB-4821) — received payload");
        service.execute();
    }
}
