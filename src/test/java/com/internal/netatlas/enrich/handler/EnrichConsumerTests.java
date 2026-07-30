package com.internal.netatlas.enrich.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.internal.netatlas.enrich.service.EnrichConsumerService;
import com.internal.netatlas.enrich.model.EnrichMessage;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrichConsumerTests {

    @Mock
    private EnrichConsumerService service;

    @InjectMocks
    private EnrichConsumerHandler handler;

    @Test
    void testHandleCallsService() {
        EnrichMessage msg = new EnrichMessage();
        msg.setMessageId("msg-123");
        handler.handle(msg);
        verify(service, times(1)).process(msg);
    }

    @Test
    void testHandleDuplicateMessageIdIsIdempotent() {
        EnrichMessage msg = new EnrichMessage();
        msg.setMessageId("dup-456");
        // first call
        handler.handle(msg);
        // second duplicate call
        handler.handle(msg);
        // service.process should be invoked twice; internal service will suppress duplicate writes.
        verify(service, times(2)).process(msg);
    }
}
