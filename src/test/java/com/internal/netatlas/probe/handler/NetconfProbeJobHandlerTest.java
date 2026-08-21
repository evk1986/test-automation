package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfIntegrationJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NetconfProbeJobHandlerTest {

    @Mock
    private NetconfIntegrationJobService netconfIntegrationJobService;

    @InjectMocks
    private NetconfProbeJobHandler netconfProbeJobHandler;

    private ProbeJobMessage validMessage;

    @BeforeEach
    void setUp() {
        validMessage = new ProbeJobMessage("JOB-NETCONF-4821", "DEV-IOSXR-99", "NETCONF", "BATCH-PRB-20240523-USE1-01", "get-config");
    }

    @Test
    @DisplayName("Should invoke processNetconfCommand when valid message is received")
    void handle_ValidMessage_InvokesService() {
        netconfProbeJobHandler.handle(validMessage);
        verify(netconfIntegrationJobService).processNetconfCommand(validMessage);
    }

    @Test
    @DisplayName("Should ignore null message without invoking service")
    void handle_NullMessage_DoesNotInvokeService() {
        netconfProbeJobHandler.handle(null);
        verify(netconfIntegrationJobService, never()).processNetconfCommand(any());
    }
}
