package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.LocalDevSetupNetconfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class LocalDevSetupNetconfHandlerTest {

    private LocalDevSetupNetconfService mockService;
    private LocalDevSetupNetconfHandler handler;

    @BeforeEach
    void setUp() {
        mockService = Mockito.mock(LocalDevSetupNetconfService.class);
        handler = new LocalDevSetupNetconfHandler(mockService);
    }

    @Test
    void shouldDelegateNetconfMessageToService() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("NETCONF");
        msg.setDeviceId("device-123");
        msg.setRawPayload("<interface><name>GigabitEthernet0/0/0</name></interface>");

        handler.handle(msg);

        verify(mockService, times(1)).process(msg);
    }

    @Test
    void shouldIgnoreNonNetconfMessage() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("SNMP");

        handler.handle(msg);

        verifyNoInteractions(mockService);
    }

    @Test
    void shouldIgnoreNullMessage() {
        handler.handle(null);
        verifyNoInteractions(mockService);
    }
}
