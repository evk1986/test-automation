package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataEnricherSqsHandler {

    private final DataEnricherService dataEnricherService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public DataEnricherSqsHandler(DataEnricherService dataEnricherService, QueueMessagingTemplate queueMessagingTemplate) {
        this.dataEnricherService = dataEnricherService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener(value = "enrich.pipeline", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void handleEnrichmentMessage(@Payload String message, @Headers Map<String, String> headers) {
        String idempotencyKey = headers.get("IdempotencyKey");
        if (idempotencyKey != null) {
            EnrichmentResult enrichmentResult = dataEnricherService.enrich(message, idempotencyKey);
            // Extend visibility timeout before enrichment step
            queueMessagingTemplate.changeMessageVisibility("enrich.pipeline", idempotencyKey, 300);
            // Process enrichment result
            System.out.println("Enrichment result: " + enrichmentResult);
        }
    }
}