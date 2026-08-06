package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.ProbeHandlersNetconfSubtreeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProbeHandlersNetconfSubtreeHandlerTest {

    @Mock
    private ProbeHandlersNetconfSubtreeService subtreeService;

    @InjectMocks
    private ProbeHandlersNetconfSubtreeHandler handler;

    private ProbeJobMessage netconfMessage;
    private ProbeJobMessage nonNetconfMessage;

    @BeforeEach
    void setUp() {
        netconfMessage = new ProbeJobMessage();
        netconfMessage.setJobId("BATCH-PRB-20240523-USE1-01");
        netconfMessage.setDeviceId("device-1234");
        netconfMessage.setProtocol("NETCONF");
        netconfMessage.setDeviceFamily("IOS-XR-NCS");

        nonNetconfMessage = new ProbeJobMessage();
        nonNetconfMessage.setJobId("BATCH-PRB-20240523-USE1-02");
        nonNetconfMessage.setDeviceId("device-5678");
        nonNetconfMessage.setProtocol("SNMP");
        nonNetconfMessage.setDeviceFamily("IOS-XR-NCS");
    }

    @Test
    void shouldProcessNetconfMessageForTargetFamily() {
        handler.handle(netconfMessage);
        verify(subtreeService, times(1)).processNetconfSubtree(netconfMessage);
    }

    @Test
    void shouldIgnoreNonNetconfProtocol() {
        handler.handle(nonNetconfMessage);
        verifyNoInteractions(subtreeService);
    }
}
