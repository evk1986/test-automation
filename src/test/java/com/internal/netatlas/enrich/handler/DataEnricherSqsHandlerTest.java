package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class DataEnricherSqsHandlerTest {

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    @Mock
    private DataEnricherService dataEnricherService;

    @InjectMocks
    private DataEnricherSqsHandler dataEnricherSqsHandler;

    @Test
    void testHandleEnrichmentMessage() {
        // Set up test data
        String message = "Test message";
        String idempotencyKey = "Test idempotency key";
        Map<String, String> headers = Map.of("IdempotencyKey", idempotencyKey);
        EnrichmentResult enrichmentResult = new EnrichmentResult("Test result id", message, idempotencyKey);

        // Mock DataEnricherService
        org.mockito.Mockito.when(dataEnricherService.enrich(message, idempotencyKey)).thenReturn(enrichmentResult);

        // Call the method under test
        dataEnricherSqsHandler.handleEnrichmentMessage(message, headers);

        // Verify the result
        assertNotNull(enrichmentResult);
    }
}