package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.model.Message;
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

@Service
public class DataEnricherSqsHandler {

    private final DataEnricherService dataEnricherService;

    @Autowired
    public DataEnricherSqsHandler(DataEnricherService dataEnricherService) {
        this.dataEnricherService = dataEnricherService;
    }

    @SqsListener(value = "enrich.pipeline", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void handle(@Payload Message message, @Headers SqsMessageHeaders headers) {
        // Process the SQS message
        EnrichmentResult enrichmentResult = dataEnricherService.processMessage(message);
        // Delete the message from the queue
        headers.getMessageAttributes().put("enrichmentResult", enrichmentResult);
    }
}