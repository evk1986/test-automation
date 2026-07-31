package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.AristaEosEnrichmentService;
import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

/**
 * Consumes normalized records from the {@code normalize.ingest} queue and delegates Arista EOS
 * records to the enrichment service.
 */
@Service
public class AristaEosEnrichmentHandler {

    private final AristaEosEnrichmentService enrichmentService;

    public AristaEosEnrichmentHandler(AristaEosEnrichmentService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @SqsListener("normalize.ingest")
    public void handle(NormalizedRecord normalizedRecord) {
        // Guard against null payloads or non‑Arista EOS records.
        if (normalizedRecord == null) {
            return;
        }
        if (!"AristaEos".equals(normalizedRecord.getCanonicalType())) {
            return;
        }
        enrichmentService.enrich(normalizedRecord);
    }
}
