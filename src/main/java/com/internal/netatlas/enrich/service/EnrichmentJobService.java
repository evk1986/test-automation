package com.internal.netatlas.enrich.service;

import com.internal.netatlas.enrich.model.EnrichmentJobMessage;
import com.internal.netatlas.enrich.repository.IdempotencyKeyRepository;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business logic for processing an enrichment job. Handles idempotency and visibility‑timeout
 * management before delegating to the actual enrichment implementation (omitted for brevity).
 */
@Service
public class EnrichmentJobService {

    private static final Logger LOG = LoggerFactory.getLogger(EnrichmentJobService.class);
    private static final int EXTENDED_TIMEOUT_SECONDS = 300; // 5 minutes

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final SqsClient sqsClient;
    private final String enrichQueueUrl;

    public EnrichmentJobService(IdempotencyKeyRepository idempotencyKeyRepository,
                                SqsClient sqsClient,
                                @org.springframework.beans.factory.annotation.Value("${aws.sqs.enrich.pipeline.url}") String enrichQueueUrl) {
        this.idempotencyKeyRepository = idempotencyKeyRepository;
        this.sqsClient = sqsClient;
        this.enrichQueueUrl = enrichQueueUrl;
    }

    /**
     * Processes the incoming message.
     *
     * @param message       the domain payload
     * @param receiptHandle SQS receipt handle used for visibility‑timeout changes
     * @param messageId     the SQS MessageId – used as the idempotency key
     */
    public void process(EnrichmentJobMessage message, String receiptHandle, String messageId) {
        // Idempotency check – if the key already exists we skip processing.
        if (idempotencyKeyRepository.existsById(messageId)) {
            LOG.info("Skipping duplicate enrichment job with idempotency key {}", messageId);
            return;
        }

        // Persist the idempotency key before any long‑running work.
        idempotencyKeyRepository.save(new com.internal.netatlas.enrich.model.IdempotencyKey(messageId));
        LOG.debug("Persisted idempotency key {}", messageId);

        // Extend visibility timeout so the message stays invisible while we enrich.
        extendVisibilityTimeout(receiptHandle, EXTENDED_TIMEOUT_SECONDS);
        try {
            // ----- Business logic placeholder -------------------------------------------------
            // In a real implementation this would invoke the enrichment pipeline, e.g.
            // enrichmentEngine.enrich(message);
            // -----------------------------------------------------------------------------------
            LOG.info("Enrichment logic executed for messageId={}", messageId);
        } finally {
            // Reset visibility timeout to the default (30 seconds) to avoid holding the lock
            // longer than necessary if the message is later re‑queued.
            extendVisibilityTimeout(receiptHandle, 30);
        }
    }

    private void extendVisibilityTimeout(String receiptHandle, int timeoutSeconds) {
        try {
            sqsClient.changeMessageVisibility(ChangeMessageVisibilityRequest.builder()
                    .queueUrl(enrichQueueUrl)
                    .receiptHandle(receiptHandle)
                    .visibilityTimeout(timeoutSeconds)
                    .build());
            LOG.debug("Extended visibility timeout to {} seconds for receiptHandle {}", timeoutSeconds, receiptHandle);
        } catch (Exception e) {
            LOG.warn("Failed to change visibility timeout for receiptHandle {}: {}", receiptHandle, e.getMessage());
            // Visibility timeout failure should not stop processing; the message may become visible
            // earlier, but the idempotency guard prevents duplicate work.
        }
    }
}
