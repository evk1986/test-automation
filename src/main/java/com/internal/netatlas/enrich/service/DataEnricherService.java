package com.internal.netatlas.enrich.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.model.NormalizedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core business logic for enriching a {@link NormalizedRecord}. The public method
 * {@link #enrichWithIdempotency(String, String)} guarantees that the same
 * {@code idempotencyKey} will not cause duplicate enrichment work.
 */
@Service
public class DataEnricherService {

    private static final Logger LOG = LoggerFactory.getLogger(DataEnricherService.class);

    // In‑memory store for processed idempotency keys. In production this would be a
    // distributed cache (Hazelcast, Redis, etc.) so that multiple pod instances share state.
    private final Map<String, Instant> processedKeys = new ConcurrentHashMap<>();

    /**
     * Enrich a record exactly once per {@code idempotencyKey}.
     *
     * @param recordId        the identifier of the {@link NormalizedRecord} to enrich
     * @param idempotencyKey  a client‑generated unique key for the request
     * @return the {@link EnrichmentResult}
     */
    public EnrichmentResult enrichWithIdempotency(String recordId, String idempotencyKey) {
        // Fast‑path check – if the key is already present we skip processing.
        Instant previous = processedKeys.putIfAbsent(idempotencyKey, Instant.now());
        if (previous != null) {
            LOG.info("Skipping enrichment for recordId={} because idempotencyKey={} was already processed at {}",
                    recordId, idempotencyKey, previous);
            // In a real system we would retrieve the previously stored result; for this demo we
            // return a lightweight placeholder indicating the request was a duplicate.
            return duplicateResult(recordId, idempotencyKey);
        }

        // Perform the actual enrichment.
        EnrichmentResult result = actualEnrich(recordId);
        LOG.debug("Enrichment succeeded for recordId={}, idempotencyKey={}", recordId, idempotencyKey);
        return result;
    }

    /**
     * Core enrichment algorithm – separated so that unit tests can spy on it.
     */
    public EnrichmentResult actualEnrich(String recordId) {
        // Simulate lookup of a NormalizedRecord; in reality this would be a repository call.
        NormalizedRecord record = new NormalizedRecord();
        record.setId(recordId);
        record.setCanonicalType("generic-device");
        record.setNormalizedPayload(JsonNodeFactory.instance.objectNode());
        record.setMappedAt(Instant.now());

        // Dummy enrichment logic – add a static field.
        Map<String, Object> enrichedFields = Collections.singletonMap("enrichedAt", Instant.now().toString());
        EnrichmentResult result = new EnrichmentResult();
        result.setId("enr-" + recordId);
        result.setNormalizedRecordId(recordId);
        result.setEnrichedFields(enrichedFields);
        result.setEnrichedAt(Instant.now());
        result.setDownstreamTopicArn("arn:aws:sns:us-east-1:123456789012:enrich-results");
        return result;
    }

    private EnrichmentResult duplicateResult(String recordId, String idempotencyKey) {
        EnrichmentResult placeholder = new EnrichmentResult();
        placeholder.setId("duplicate-" + recordId);
        placeholder.setNormalizedRecordId(recordId);
        placeholder.setEnrichedFields(Collections.singletonMap("duplicateKey", idempotencyKey));
        placeholder.setEnrichedAt(Instant.now());
        placeholder.setDownstreamTopicArn("arn:aws:sns:us-east-1:123456789012:enrich-results");
        return placeholder;
    }
}
