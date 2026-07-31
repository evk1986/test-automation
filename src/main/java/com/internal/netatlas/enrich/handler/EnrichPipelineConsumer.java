package com.internal.netatlas.enrich.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import com.internal.netatlas.enrich.service.EnrichmentProcessingService;

/**
 * SQS consumer for the {@code enrich.pipeline} queue. It receives {@link EnrichmentMessage}
 * objects, validates them and forwards them to {@link EnrichmentProcessingService} for
 * idempotent persistence and metric collection.
 */
@Service
@Slf4j
public class EnrichPipelineConsumer {

    private final EnrichmentProcessingService processingService;

    public EnrichPipelineConsumer(EnrichmentProcessingService processingService) {
        this.processingService = processingService;
    }

    @SqsListener("enrich.pipeline")
    public void handle(EnrichmentMessage message) {
        if (message == null) {
            log.warn("Received null EnrichmentMessage – ignoring");
            return;
        }
        log.debug("Processing enrichment message for deviceId={}, normalizedRecordId={}",
                message.getDeviceId(), message.getNormalizedRecordId());
        processingService.process(message);
    }

    /**
     * Minimal DTO representing the payload that arrives on the {@code enrich.pipeline} queue.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnrichmentMessage {
        private String deviceId;
        private String normalizedRecordId;
        private String payload; // JSON string with raw enrichment data
    }
}
