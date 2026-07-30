package com.internal.netatlas.probe.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnrichConsumerHandlerTest {
    @Mock private EnrichConsumerService service;
    @InjectMocks private EnrichConsumerHandler handler;

    @Test
    void delegatesToService() {
        handler.handle("TES-109-test-payload");
        verify(service).execute();
    }
}
