package com.internal.netatlas.enrich.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.internal.netatlas.enrich.model.EnrichmentMessage;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service that performs idempotent enrichment processing.
 *
 * <ul>
 *   <li>Checks the {@code EnrichmentResultRepository} for an existing record using the SQS messageId as the primary key.</li>
 *   <li>If the record does not exist, creates a new {@link EnrichmentResult} and persists it.</li>
 *   <li>Extends the SQS visibility timeout to accommodate long‑running enrichment steps (minimum 300 seconds).</li>
 * </ul>
 */
@Service
public class DataEnricherBatchService {

    private static final Logger LOG = LoggerFactory.getLogger(DataEnricherBatchService.class);

    private final EnrichmentResultRepository repository;
    private final AmazonSQS sqsClient;
    private final String queueUrl;

    public DataEnricherBatchService(EnrichmentResultRepository repository,
                                    AmazonSQS sqsClient,
                                    @Value("${aws.sqs.enrich.pipeline.url}") String queueUrl) {
        this.repository = repository;
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }

    /**
     * Processes a single enrichment message.
     *
     * @param message the incoming SQS payload
     */
    public void processMessage(EnrichmentMessage message) {
        // Idempotency check – the messageId is used as the primary key in Cassandra.
        Optional<EnrichmentResult> existing = repository.findById(message.getMessageId());
        if (existing.isPresent()) {
            LOG.info("Enrichment result already exists for messageId={}, skipping write", message.getMessageId());
            // Still extend visibility to avoid immediate redelivery of a duplicate that may be stuck.
            extendVisibilityTimeout(message.getReceiptHandle());
            return;
        }

        // Build the enrichment result entity.
        EnrichmentResult result = new EnrichmentResult();
        result.setId(UUID.randomUUID().toString());
        result.setMessageId(message.getMessageId());
        result.setNormalizedRecordId(message.getNormalizedRecordId());
        result.setEnrichedFields(message.getEnrichedFields());
        result.setEnrichedAt(Instant.now());
        result.setDownstreamTopicArn(message.getDownstreamTopicArn());

        repository.save(result);
        LOG.info("Enrichment result persisted for messageId={}", message.getMessageId());

        // Extend visibility timeout for the current message.
        extendVisibilityTimeout(message.getReceiptHandle());
    }

    private void extendVisibilityTimeout(String receiptHandle) {
        try {
            ChangeMessageVisibilityRequest request = new ChangeMessageVisibilityRequest()
                    .withQueueUrl(queueUrl)
                    .withReceiptHandle(receiptHandle)
                    .withVisibilityTimeout(300); // seconds
            sqsClient.changeMessageVisibility(request);
            LOG.debug("Visibility timeout extended to 300s for receiptHandle={}", receiptHandle);
        } catch (Exception e) {
            LOG.error("Failed to extend visibility timeout for receiptHandle={}", receiptHandle, e);
        }
    }
}
