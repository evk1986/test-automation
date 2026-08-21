package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Orch882ExposeRapidPollHandler {
    private static final Logger log = LoggerFactory.getLogger(Orch882ExposeRapidPollHandler.class);
    private final Orch882ExposeRapidPollService service;

    public Orch882ExposeRapidPollHandler(Orch882ExposeRapidPollService service) {
        this.service = service;
    }

    // Queue: device-probe-jobs
    public void handle(String payload) {
        log.info("ORCH-882: Expose rapid‑poll queue depth metric and auto‑scaling hook in Fleet‑Or — received payload");
        service.execute();
    }
}
