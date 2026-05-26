package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.AWSSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataEnricherSqsConsumer {
    private final DataEnricherService dataEnricherService;
    private final AWSSQS awsSqs;

    @Autowired
    public DataEnricherSqsConsumer(DataEnricherService dataEnricherService, AWSSQS awsSqs) {
        this.dataEnricherService = dataEnricherService;
        this.awsSqs = awsSqs;
    }

    public void consumeMessages() {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest("enrich.pipeline");
        List<Message> messages = awsSqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            if (dataEnricherService.isIdempotent(message)) {
                System.out.println("Message is idempotent, skipping...");
            } else {
                EnrichmentResult enrichmentResult = dataEnricherService.enrich(message);
                System.out.println("Enrichment result: " + enrichmentResult);
            }
        }
    }
}