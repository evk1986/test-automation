package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfBatchProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NetconfBatchHandlerTest {

    @Mock
    private NetconfBatchProcessingService processingService;

    @InjectMocks
    private NetconfBatchHandler handler;

    @Test
    void shouldDelegateNetconfMessageToProcessingService() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("NETCONF");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");

        handler.handle(msg);

        verify(processingService, times(1)).processBatch("BATCH-PRB-20240523-USE1-01");
    }

    @Test
    void shouldIgnoreNonNetconfMessage() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("SNMP");
        msg.setBatchId("BATCH-PRB-20240523-USE1-01");

        handler.handle(msg);

        verifyNoInteractions(processingService);
    }
}
