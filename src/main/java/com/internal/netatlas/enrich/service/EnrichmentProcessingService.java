package com.internal.netatlas.enrich.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.enrich.handler.EnrichPipelineConsumer.EnrichmentMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.repository.CassandraRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Business logic for persisting enrichment results in an idempotent fashion.
 * It also records a Micrometer counter for each successful write.
 */
@Service
@Slf4j
public class EnrichmentProcessingService {

    private final EnrichmentResultRepository repository;
    private final Counter enrichmentCounter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EnrichmentProcessingService(EnrichmentResultRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.enrichmentCounter = Counter.builder("enricher.results.written")
                .description("Number of enrichment results successfully persisted")
                .register(meterRegistry);
    }

    /**
     * Persists the enrichment payload if a record with the same {@code normalizedRecordId}
     * does not already exist. The method is safe to call multiple times with the same
     * message – only the first call results in a Cassandra write.
     */
    public void process(EnrichmentMessage message) {
        try {
            Optional<EnrichmentResult> existing = repository.findById(message.getNormalizedRecordId());
            if (existing.isPresent()) {
                log.info("Duplicate enrichment detected for normalizedRecordId={}, skipping write",
                        message.getNormalizedRecordId());
                return;
            }

            JsonNode payloadNode = objectMapper.readTree(message.getPayload());
            EnrichmentResult result = new EnrichmentResult();
            result.setId(Uuids.timeBased().toString());
            result.setNormalizedRecordId(message.getNormalizedRecordId());
            result.setDeviceId(message.getDeviceId());
            result.setEnrichedFields(objectMapper.convertValue(payloadNode, Map.class));
            result.setEnrichedAt(Instant.now());

            repository.save(result);
            enrichmentCounter.increment();
            log.info("Enrichment result persisted for normalizedRecordId={}", message.getNormalizedRecordId());
        } catch (Exception e) {
            log.error("Failed to process enrichment message for normalizedRecordId={}",
                    message.getNormalizedRecordId(), e);
            // In a real implementation we would move the message to a DLQ after retries.
        }
    }

    /**
     * Cassandra repository for {@link EnrichmentResult}. The concrete implementation is
     * provided by Spring Data Cassandra at runtime.
     */
    public interface EnrichmentResultRepository extends CassandraRepository<EnrichmentResult, String> {
    }

    /**
     * Simple DTO representing a persisted enrichment result.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnrichmentResult {
        private String id;
        private String normalizedRecordId;
        private String deviceId;
        private Map<String, Object> enrichedFields;
        private Instant enrichedAt;
    }
}
