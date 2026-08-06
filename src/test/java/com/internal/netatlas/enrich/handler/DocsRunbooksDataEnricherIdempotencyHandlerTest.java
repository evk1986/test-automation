package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.DocsRunbooksDataEnricherIdempotencyService;
import com.internal.netatlas.enrich.handler.DocsRunbooksDataEnricherIdempotencyHandler.EnrichmentMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DocsRunbooksDataEnricherIdempotencyHandlerTest {

    @Mock
    private DocsRunbooksDataEnricherIdempotencyService service;

    @InjectMocks
    private DocsRunbooksDataEnricherIdempotencyHandler handler;

    private EnrichmentMessage sampleMessage;

    @BeforeEach
    void setUp() {
        sampleMessage = new EnrichmentMessage(
                "idemp-12345",
                "device-9876",
                "{\"interface\":\"Gig0/1\"}"
        );
    }

    @Test
    void handle_ForwardsMessageToService() {
        handler.handle(sampleMessage);
        verify(service, times(1)).processMessage(sampleMessage);
    }
}
