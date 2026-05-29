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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataEnricherSqsHandlerTest {

    @Mock
    private DataEnricherService dataEnricherService;

    @InjectMocks
    private DataEnricherSqsHandler dataEnricherSqsHandler;

    @Test
    public void testHandle() {
        // Create a test message
        Message message = new Message();
        // Set up the mock service
        when(dataEnricherService.processMessage(message)).thenReturn(new EnrichmentResult());
        // Call the handle method
        dataEnricherSqsHandler.handle(message, null);
        // Verify the result
        assertNotNull(dataEnricherService.processMessage(message));
    }
}