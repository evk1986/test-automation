package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

/**
 * SQS consumer for the {@code enrich.pipeline} queue. It guarantees exactly‑once processing by
 * using an idempotency key supplied in the message payload. The consumer also extends the
 * visibility timeout while the enrichment work is in progress to avoid premature redelivery.
 */
@Service
public class ComTelecomPipelineEnricherConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ComTelecomPipelineEnricherConsumer.class);

    private final DataEnricherService enricherService;
    private final AmazonSQS amazonSQS;
    private final String queueUrl;
    private final int visibilityExtensionSeconds;

    public ComTelecomPipelineEnricherConsumer(DataEnricherService enricherService,
                                              AmazonSQS amazonSQS,
                                              @Value("${aws.sqs.enrich.pipeline.url}") String queueUrl,
                                              @Value("${aws.sqs.visibility.extension.seconds:300}") int visibilityExtensionSeconds) {
        this.enricherService = enricherService;
        this.amazonSQS = amazonSQS;
        this.queueUrl = queueUrl;
        this.visibilityExtensionSeconds = visibilityExtensionSeconds;
    }

    @SqsListener("enrich.pipeline")
    public void handle(Message sqsMessage) {
        String receiptHandle = sqsMessage.getReceiptHandle();
        String body = sqsMessage.getBody();
        // Expected JSON: {"recordId":"...","idempotencyKey":"..."}
        EnrichmentMessage msg = EnrichmentMessage.fromJson(body);
        LOG.info("Received enrichment request for recordId={}, idempotencyKey={}", msg.getRecordId(), msg.getIdempotencyKey());

        // Extend visibility before processing – this gives the worker extra time.
        extendVisibilityTimeout(receiptHandle);

        try {
            EnrichmentResult result = enricherService.enrichWithIdempotency(msg.getRecordId(), msg.getIdempotencyKey());
            LOG.info("Enrichment completed for recordId={}, idempotencyKey={}", msg.getRecordId(), msg.getIdempotencyKey());
            // Successful processing – delete the message.
            deleteMessage(receiptHandle);
        } catch (Exception e) {
            LOG.error("Enrichment failed for recordId={}, idempotencyKey={}. Leaving message for retry.",
                    msg.getRecordId(), msg.getIdempotencyKey(), e);
            // Do not delete; message will become visible again after original timeout.
        }
    }

    private void extendVisibilityTimeout(String receiptHandle) {
        try {
            ChangeMessageVisibilityRequest request = new ChangeMessageVisibilityRequest()
                    .withQueueUrl(queueUrl)
                    .withReceiptHandle(receiptHandle)
                    .withVisibilityTimeout(visibilityExtensionSeconds);
            amazonSQS.changeMessageVisibility(request);
            LOG.debug("Visibility timeout extended by {} seconds for receiptHandle={}", visibilityExtensionSeconds, receiptHandle);
        } catch (Exception e) {
            LOG.warn("Failed to extend visibility timeout for receiptHandle={}", receiptHandle, e);
        }
    }

    private void deleteMessage(String receiptHandle) {
        DeleteMessageRequest deleteRequest = new DeleteMessageRequest()
                .withQueueUrl(queueUrl)
                .withReceiptHandle(receiptHandle);
        amazonSQS.deleteMessage(deleteRequest);
    }
}

/**
 * Simple DTO representing the payload that arrives on the {@code enrich.pipeline} queue.
 * In a real code base this would be a proper POJO with Jackson annotations.
 */
class EnrichmentMessage {
    private final String recordId;
    private final String idempotencyKey;

    private EnrichmentMessage(String recordId, String idempotencyKey) {
        this.recordId = recordId;
        this.idempotencyKey = idempotencyKey;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public static EnrichmentMessage fromJson(String json) {
        // Very lightweight parsing – avoid pulling in a full JSON library for this example.
        // Expected format: {"recordId":"abc","idempotencyKey":"def"}
        String cleaned = json.replaceAll("[{}\\"]", "");
        String[] parts = cleaned.split(",");
        String recordId = null;
        String idempotencyKey = null;
        for (String part : parts) {
            String[] kv = part.split(":");
            if (kv.length != 2) continue;
            String key = kv[0].trim();
            String value = kv[1].trim();
            if ("recordId".equals(key)) {
                recordId = value;
            } else if ("idempotencyKey".equals(key)) {
                idempotencyKey = value;
            }
        }
        if (recordId == null || idempotencyKey == null) {
            throw new IllegalArgumentException("Invalid enrichment message payload: " + json);
        }
        return new EnrichmentMessage(recordId, idempotencyKey);
    }
}
