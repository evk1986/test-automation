package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.handler.DocsRunbooksDataEnricherIdempotencyHandler.EnrichmentMessage;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

/**
 * Service that guarantees idempotent processing of enrichment messages.
 * It stores a lightweight idempotency record in Cassandra; duplicate messages are ignored.
 */
@Service
public class DocsRunbooksDataEnricherIdempotencyService {

    private static final Logger LOG = LoggerFactory.getLogger(DocsRunbooksDataEnricherIdempotencyService.class);
    private final EnrichmentResultRepository repository;

    public DocsRunbooksDataEnricherIdempotencyService(EnrichmentResultRepository repository) {
        this.repository = repository;
    }

    /**
     * Process a single enrichment message.
     * If the idempotency key already exists, the method logs and returns without side‑effects.
     * Otherwise it persists a new {@link EnrichmentResult} record.
     */
    public void processMessage(EnrichmentMessage message) {
        String key = message.getIdempotencyKey();
        if (repository.findById(key).isPresent()) {
            LOG.warn("Duplicate enrichment message detected for idempotencyKey={}. Skipping processing.", key);
            return;
        }
        EnrichmentResult result = new EnrichmentResult(
                key,
                message.getDeviceId(),
                message.getPayload(),
                Instant.now()
        );
        repository.save(result);
        LOG.info("Enrichment result persisted for idempotencyKey={}", key);
        // In a real implementation, further enrichment steps and SNS publishing would follow.
    }

    /**
     * Minimal Cassandra entity representing a processed enrichment record.
     */
    public static class EnrichmentResult {
        private final String idempotencyKey;
        private final String deviceId;
        private final String rawPayload;
        private final Instant processedAt;

        public EnrichmentResult(String idempotencyKey, String deviceId, String rawPayload, Instant processedAt) {
            this.idempotencyKey = idempotencyKey;
            this.deviceId = deviceId;
            this.rawPayload = rawPayload;
            this.processedAt = processedAt;
        }

        public String getIdempotencyKey() {
            return idempotencyKey;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getRawPayload() {
            return rawPayload;
        }

        public Instant getProcessedAt() {
            return processedAt;
        }
    }
}
