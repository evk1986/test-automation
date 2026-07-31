package com.internal.netatlas.enrich.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.internal.netatlas.enrich.handler.EnrichmentMessage;
import com.internal.netatlas.enrich.repository.IdempotencyRepository;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

/**
 * Core business logic for the Data‑Enricher service.
 *
 * <ul>
 *   <li>Ensures exactly‑once processing by checking an idempotency table in Cassandra.</li>
 *   <li>Extends the SQS visibility timeout to avoid premature redelivery while enrichment runs.</li>
 *   <li>Persists the enrichment result.</li>
 * </ul>
 */
@Service
public class EnricherProcessingService {

    private final IdempotencyRepository idempotencyRepository;
    private final EnrichmentResultRepository resultRepository;
    private final AmazonSQS sqsClient;
    private final String queueUrl;

    @Autowired
    public EnricherProcessingService(IdempotencyRepository idempotencyRepository,
                                      EnrichmentResultRepository resultRepository,
                                      AmazonSQS sqsClient) {
        this.idempotencyRepository = idempotencyRepository;
        this.resultRepository = resultRepository;
        this.sqsClient = sqsClient;
        // In a real deployment this would be resolved from configuration
        this.queueUrl = System.getenv("ENRICH_PIPELINE_QUEUE_URL");
    }

    /**
     * Executes the enrichment workflow.
     *
     * @param message               the incoming SQS payload
     * @param idempotencyKey        the SQS MessageId used as a unique key
     * @param receiptHandle         SQS receipt handle required for visibility timeout changes
     * @param visibilityExtension   seconds to extend the visibility timeout
     */
    public void process(EnrichmentMessage message,
                        String idempotencyKey,
                        String receiptHandle,
                        int visibilityExtension) {
        // 1. Idempotency check – if the key already exists we skip processing.
        if (idempotencyRepository.exists(idempotencyKey)) {
            // Duplicate detected – nothing else to do.
            return;
        }

        // 2. Extend visibility timeout to give the enrichment step enough time.
        extendVisibilityTimeout(receiptHandle, visibilityExtension);

        // 3. Perform enrichment (placeholder for real business logic).
        EnrichmentResult result = performEnrichment(message);

        // 4. Persist the result.
        resultRepository.save(result);

        // 5. Record the idempotency key after a successful write.
        idempotencyRepository.save(idempotencyKey, Instant.now());
    }

    private void extendVisibilityTimeout(String receiptHandle, int timeoutSeconds) {
        ChangeMessageVisibilityRequest request = new ChangeMessageVisibilityRequest()
                .withQueueUrl(queueUrl)
                .withReceiptHandle(receiptHandle)
                .withVisibilityTimeout(timeoutSeconds);
        sqsClient.changeMessageVisibility(request);
    }

    private EnrichmentResult performEnrichment(EnrichmentMessage message) {
        // Minimal stub – in production this would invoke cross‑reference look‑ups,
        // derived‑field calculations, etc.
        EnrichmentResult result = new EnrichmentResult();
        result.setNormalizedRecordId(message.getNormalizedRecordId());
        result.setEnrichedFields(message.getPayload()); // echo payload as enriched fields
        result.setEnrichedAt(Instant.now());
        result.setDownstreamTopicArn("arn:aws:sns:us-east-1:123456789012:enricher-output");
        return result;
    }
}

/** Simple DTO representing the SQS payload for the enrichment stage. */
class EnrichmentMessage {
    private String normalizedRecordId;
    private String protocol;
    private String region;
    private java.util.Map<String, Object> payload;

    public String getNormalizedRecordId() { return normalizedRecordId; }
    public void setNormalizedRecordId(String id) { this.normalizedRecordId = id; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public java.util.Map<String, Object> getPayload() { return payload; }
    public void setPayload(java.util.Map<String, Object> payload) { this.payload = payload; }
}

/** Simple DTO for the enrichment result persisted to Cassandra. */
class EnrichmentResult {
    private String normalizedRecordId;
    private java.util.Map<String, Object> enrichedFields;
    private java.time.Instant enrichedAt;
    private String downstreamTopicArn;

    public String getNormalizedRecordId() { return normalizedRecordId; }
    public void setNormalizedRecordId(String id) { this.normalizedRecordId = id; }
    public java.util.Map<String, Object> getEnrichedFields() { return enrichedFields; }
    public void setEnrichedFields(java.util.Map<String, Object> fields) { this.enrichedFields = fields; }
    public java.time.Instant getEnrichedAt() { return enrichedAt; }
    public void setEnrichedAt(java.time.Instant enrichedAt) { this.enrichedAt = enrichedAt; }
    public String getDownstreamTopicArn() { return downstreamTopicArn; }
    public void setDownstreamTopicArn(String arn) { this.downstreamTopicArn = arn; }
}

/** Repository stub for idempotency tracking. */
interface IdempotencyRepository {
    boolean exists(String idempotencyKey);
    void save(String idempotencyKey, Instant processedAt);
}

/** Repository stub for persisting enrichment results. */
interface EnrichmentResultRepository {
    void save(EnrichmentResult result);
}
