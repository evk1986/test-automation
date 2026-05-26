package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataEnricherSqsHandler {

    private final DataEnricherService dataEnricherService;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final AmazonSQS amazonSQS;

    @Autowired
    public DataEnricherSqsHandler(DataEnricherService dataEnricherService, QueueMessagingTemplate queueMessagingTemplate, AmazonSQS amazonSQS) {
        this.dataEnricherService = dataEnricherService;
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.amazonSQS = amazonSQS;
    }

    public void handle(Message message) {
        String idempotencyKey = message.getMessageAttributes().get("idempotencyKey").getStringValue();
        if (dataEnricherService.isAlreadyProcessed(idempotencyKey)) {
            // If the message is already processed, skip it
            return;
        }
        EnrichmentResult enrichmentResult = dataEnricherService.enrich(message.getBody());
        queueMessagingTemplate.convertAndSend("enrich.pipeline", enrichmentResult);
        dataEnricherService.markAsProcessed(idempotencyKey);
    }
}