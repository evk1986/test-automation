package com/internal/netatlas/probe;

import com/internal/netatlas/probe.handler.NetconfCiscoIosXrNcsHandler;
import com.internal/netatlas/probe.model.ProbeJobMessage;
import com.internal/netatlas/probe.protocol.NetconfAdapter;
import com.internal/netatlas/probe.service.NetconfBatchRetryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

@ExtendWith(MockitoExtension.class)
public class NetconfCiscoIosXrNcsHandlerTest {
    @Mock
    private NetconfAdapter netconfAdapter;
    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;
    @Mock
    private NetconfBatchRetryService netconfBatchRetryService;
    @InjectMocks
    private NetconfCiscoIosXrNcsHandler handler;

    @Test
    public void testHandle() {
        // Create a probe job message
        ProbeJobMessage message = new ProbeJobMessage("device-id", "NETCONF");
        // Mock NETCONF adapter and queue messaging template
        // Call the handle method
        handler.handle(message);
        // Verify that the NETCONF adapter and queue messaging template were called
    }
}