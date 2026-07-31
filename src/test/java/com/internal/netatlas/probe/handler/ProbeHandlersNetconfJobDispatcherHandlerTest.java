package com.internal.netatlas.probe.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProbeHandlersNetconfJobDispatcherHandlerTest {
    @Mock private ProbeHandlersNetconfJobDispatcherService service;
    @InjectMocks private ProbeHandlersNetconfJobDispatcherHandler handler;

    @Test
    void delegatesToService() {
        handler.handle("TES-112-test-payload");
        verify(service).execute();
    }
}
