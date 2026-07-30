package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfNcsProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EndToEndIntegrationTest {
    @Mock
    private NetconfNcsProcessingService processingService;

    @InjectMocks
    private NetconfNcsHandler handler;

    private ProbeJobMessage netconfMessage;
    private ProbeJobMessage sshMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        netconfMessage = new ProbeJobMessage();
        netconfMessage.setJobId("JOB-NETCONF-4821");
        netconfMessage.setDeviceId("device-123");
        netconfMessage.setProtocol("NETCONF");
        netconfMessage.setDeviceFamily("IOS-XR_NCS");

        sshMessage = new ProbeJobMessage();
        sshMessage.setJobId("JOB-SSH-0001");
        sshMessage.setDeviceId("device-999");
        sshMessage.setProtocol("SSH");
        sshMessage.setDeviceFamily("IOS-XR_NCS");
    }

    @Test
    void shouldDelegateNetconfMessageToProcessingService() {
        handler.handle(netconfMessage);
        verify(processingService, times(1)).process(netconfMessage);
    }

    @Test
    void shouldIgnoreNonNetconfMessage() {
        handler.handle(sshMessage);
        verifyNoInteractions(processingService);
    }

    @Test
    void shouldHandleNullMessageGracefully() {
        // No exception should be thrown
        assertDoesNotThrow(() -> handler.handle(null));
        verifyNoInteractions(processingService);
    }
}
