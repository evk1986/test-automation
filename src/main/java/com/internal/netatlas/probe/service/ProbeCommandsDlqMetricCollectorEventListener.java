package com.internal.netatlas.probe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProbeCommandsDlqMetricCollectorEventListener {
    private static final Logger log = LoggerFactory.getLogger(ProbeCommandsDlqMetricCollectorEventListener.class);
    private final ProbeCommandsDlqMetricCollectorService service;

    public ProbeCommandsDlqMetricCollectorEventListener(ProbeCommandsDlqMetricCollectorService service) {
        this.service = service;
    }

    @EventListener
    public void onReady(ApplicationReadyEvent event) {
        // Add DLQ visibility metrics and automated drain endpoint for probe.commands (ORCH — post-startup hook
        log.info("ProbeCommandsDlqMetricCollector event listener initialized — validating service availability");
        service.execute();
    }
}
