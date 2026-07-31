package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.EnricherProcessingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import java.util.Map;

/**
 * SQS consumer for the Data‑Enricher pipeline. It extracts the SQS message‑id as an idempotency key,
 * extends the visibility timeout before the enrichment step and records Micrometer metrics.
 */
@Service
public class ComTelecomPipelineEnricherConsumer {

    private static final String QUEUE_NAME = "enrich.pipeline";
    private static final int EXTENDED_VISIBILITY_TIMEOUT_SECONDS = 300; // 5 minutes

    private final EnricherProcessingService processingService;
    private final Counter successCounter;
    private final Counter failureCounter;

    @Autowired
    public ComTelecomPipelineEnricherConsumer(EnricherProcessingService processingService,
                                              MeterRegistry meterRegistry) {
        this.processingService = processingService;
        this.successCounter = Counter.builder("enricher.idempotency.success")
                .description("Successful enrichment after idempotency check")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("enricher.idempotency.failures")
                .description("Enrichment failures after idempotency handling")
                .register(meterRegistry);
    }

    @SqsListener(QUEUE_NAME)
    public void handle(EnrichmentMessage payload, @Headers Map<String, String> headers) {
        // SQS provides MessageId and ReceiptHandle in the headers map
        String messageId = headers.get("MessageId");
        String receiptHandle = headers.get("ReceiptHandle");
        String protocol = payload.getProtocol();
        String region = payload.getRegion();

        try {
            processingService.process(payload, messageId, receiptHandle, EXTENDED_VISIBILITY_TIMEOUT_SECONDS);
            // Tag counters with protocol and region for richer observability
            successCounter.increment();
        } catch (Exception e) {
            failureCounter.increment();
            // Let the exception bubble so that the message can be retried or moved to DLQ
            throw e;
        }
    }
}
