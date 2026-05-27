package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfSubtreeHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NetconfSubtreeHandlerTest {

    @Mock
    private NetconfAdapter netconfAdapter;

    @Mock
    private NetconfBatchRetryService netconfBatchRetryService;

    @InjectMocks
    private NetconfSubtreeHandler handler;

    @Test
    void testHandle() {
        // Create a test message
        ProbeJobMessage message = new ProbeJobMessage("NETCONF", "Cisco IOS-XR NCS", "device-id");
        // Call the handle method
        handler.handle(message);
        // Verify that the NetconfAdapter and NetconfBatchRetryService were called
        verify(netconfAdapter).sendSubtreeRequest(any(), any());
        verify(netconfBatchRetryService).updateJobStatus(any(), any());
    }
}