package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.model.EnrichmentJobMessage;
import com.internal.netatlas.enrich.service.EnrichmentJobService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQS consumer for the Data‑Enricher pipeline. It extracts an idempotency key from the SQS
 * {@code MessageId}, persists it, and extends the message visibility timeout while the long‑running
 * enrichment logic executes.
 */
@Service
public class EnrichmentJobHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EnrichmentJobHandler.class);

    private final EnrichmentJobService enrichmentJobService;
    private final MeterRegistry meterRegistry;
    private final SqsClient sqsClient;
    private final String enrichQueueUrl;

    public EnrichmentJobHandler(EnrichmentJobService enrichmentJobService,
                                MeterRegistry meterRegistry,
                                SqsClient sqsClient,
                                @Value("${aws.sqs.enrich.pipeline.url}") String enrichQueueUrl) {
        this.enrichmentJobService = enrichmentJobService;
        this.meterRegistry = meterRegistry;
        this.sqsClient = sqsClient;
        this.enrichQueueUrl = enrichQueueUrl;
    }

    @SqsListener("enrich.pipeline")
    public void handle(EnrichmentJobMessage message, String receiptHandle, String messageId) {
        LOG.info("Received enrichment job messageId={}, protocol={}, region={}",
                messageId, message.getProtocol(), message.getRegion());
        try {
            enrichmentJobService.process(message, receiptHandle, messageId);
            // Delete the message only after successful processing
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(enrichQueueUrl)
                    .receiptHandle(receiptHandle)
                    .build());
        } catch (Exception e) {
            LOG.error("Enrichment job failed for messageId={}: {}", messageId, e.getMessage(), e);
            // Increment failure counter with protocol and region tags
            meterRegistry.counter("enricher.failures",
                    "protocol", message.getProtocol(),
                    "region", message.getRegion())
                    .increment();
            // Let the message become visible again after the visibility timeout expires
        }
    }
}
