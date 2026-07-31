package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.DocsRunbooksDataEnricherIdempotencyService;
import com.internal.netatlas.enrich.handler.DocsRunbooksDataEnricherIdempotencyHandler.EnrichmentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

/**
 * SQS handler that receives enrichment messages and forwards them to the idempotency service.
 * Queue: {@code enrich.pipeline}
 */
@Service
public class DocsRunbooksDataEnricherIdempotencyHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DocsRunbooksDataEnricherIdempotencyHandler.class);
    private final DocsRunbooksDataEnricherIdempotencyService idempotencyService;

    public DocsRunbooksDataEnricherIdempotencyHandler(DocsRunbooksDataEnricherIdempotencyService idempotencyService) {
        this.idempotencyService = idempotencyService;
    }

    @SqsListener("enrich.pipeline")
    public void handle(EnrichmentMessage message) {
        LOG.info("Received enrichment message idempotencyKey={}, deviceId={}", message.getIdempotencyKey(), message.getDeviceId());
        idempotencyService.processMessage(message);
    }

    /**
     * Minimal DTO representing the payload arriving on the {@code enrich.pipeline} queue.
     */
    public static class EnrichmentMessage {
        private final String idempotencyKey;
        private final String deviceId;
        private final String payload; // raw JSON string from the normalizer

        public EnrichmentMessage(String idempotencyKey, String deviceId, String payload) {
            this.idempotencyKey = idempotencyKey;
            this.deviceId = deviceId;
            this.payload = payload;
        }

        public String getIdempotencyKey() {
            return idempotencyKey;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getPayload() {
            return payload;
        }
    }
}
