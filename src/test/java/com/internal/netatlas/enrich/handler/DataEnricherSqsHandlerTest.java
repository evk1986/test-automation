package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataEnricherSqsHandlerTest {

    @Mock
    private DataEnricherService dataEnricherService;

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    @Mock
    private AmazonSQS amazonSQS;

    @InjectMocks
    private DataEnricherSqsHandler dataEnricherSqsHandler;

    @Test
    void testHandle() {
        // Given
        Message message = new Message("messageBody");
        message.setMessageAttributes(any());
        when(dataEnricherService.isAlreadyProcessed(any())).thenReturn(false);
        when(dataEnricherService.enrich(any())).thenReturn(new EnrichmentResult("id", "messageBody"));

        // When
        dataEnricherSqsHandler.handle(message);

        // Then
        // Verify the enrichment result is sent to the queue
    }
}