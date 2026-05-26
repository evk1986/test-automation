package com.internal.netatlas.probe;

import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.probe.handler.NetconfRetryHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

@ExtendWith(MockitoExtension.class)
public class NetconfRetryHandlerTest {

    @InjectMocks
    private NetconfRetryHandler netconfRetryHandler;

    @Test
    void testHandle() {
        // Create a test message
        ProbeJobMessage message = new ProbeJobMessage();
        message.setBatchId("test-batch-id");

        // Simulate the handle method
        netconfRetryHandler.handle(message);
    }
}