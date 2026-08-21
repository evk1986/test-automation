package com.internal.netatlas.probe.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CiscoIosXeNetconfSubtreeHandlerTest {
    @Mock private CiscoIosXeNetconfSubtreeService service;
    @InjectMocks private CiscoIosXeNetconfSubtreeHandler handler;

    @Test
    void delegatesToService() {
        handler.handle("TES-192-test-payload");
        verify(service).execute();
    }
}
