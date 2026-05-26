package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataEnricherSqsConsumer {
    private final AmazonSQS sqs;
    private final DataEnricherService dataEnricherService;

    @Autowired
    public DataEnricherSqsConsumer(AmazonSQS sqs, DataEnricherService dataEnricherService) {
        this.sqs = sqs;
        this.dataEnricherService = dataEnricherService;
    }

    public void consume() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest("enrich.pipeline").withWaitTimeSeconds(10);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            String idempotencyKey = message.getMessageAttributes().get("IdempotencyKey").getStringValue();
            if (dataEnricherService.isDuplicate(idempotencyKey)) {
                sqs.deleteMessage("enrich.pipeline", message.getReceiptHandle());
            } else {
                EnrichmentResult enrichmentResult = dataEnricherService.enrich(message.getBody());
                sqs.deleteMessage("enrich.pipeline", message.getReceiptHandle());
            }
        }
    }
}