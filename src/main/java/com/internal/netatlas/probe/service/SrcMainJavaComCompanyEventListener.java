package com.internal.netatlas.probe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SrcMainJavaComCompanyEventListener {
    private static final Logger log = LoggerFactory.getLogger(SrcMainJavaComCompanyEventListener.class);
    private final SrcMainJavaComCompanyService service;

    public SrcMainJavaComCompanyEventListener(SrcMainJavaComCompanyService service) {
        this.service = service;
    }

    @EventListener
    public void onReady(ApplicationReadyEvent event) {
        // Enhance Data‑Enricher SQS consumer with idempotency key and visibility‑timeout l — post-startup hook
        log.info("SrcMainJavaComCompany event listener initialized — validating service availability");
        service.execute();
    }
}
