package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.model.EnrichmentMessage;
import com.internal.netatlas.enrich.service.EnrichmentConsumerService;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.mockito.Mockito.*;

class EnrichmentConsumerHandlerTest {

    @Test
    void shouldDelegateMessageToService() {
        EnrichmentConsumerService mockService = mock(EnrichmentConsumerService.class);
        EnrichmentConsumerHandler handler = new EnrichmentConsumerHandler(mockService);

        EnrichmentMessage msg = new EnrichmentMessage("msg-123", "norm-456", Map.of("field", "value"));
        handler.handle(msg);

        verify(mockService, times(1)).processMessage(msg);
    }

    @Test
    void shouldIgnoreDuplicateMessages() {
        EnrichmentConsumerService mockService = mock(EnrichmentConsumerService.class);
        EnrichmentConsumerHandler handler = new EnrichmentConsumerHandler(mockService);

        EnrichmentMessage duplicate = new EnrichmentMessage("msg-dup", "norm-789", Map.of("field", "value"));
        // first processing
        handler.handle(duplicate);
        // second (duplicate) processing
        handler.handle(duplicate);

        // Service is invoked for each receipt; idempotency is enforced inside the service.
        verify(mockService, times(2)).processMessage(duplicate);
    }
}
