package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfCiscoIosXrNcsHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class NetconfCiscoIosXrNcsHandlerTest {

    @InjectMocks
    private NetconfCiscoIosXrNcsHandler handler;

    @Test
    void testHandle() {
        // Create a sample ProbeJobMessage
        ProbeJobMessage message = new ProbeJobMessage();
        message.setDeviceId("device-123");
        message.setProtocol("NETCONF");

        // Call the handle method and verify the result
        handler.handle(message);
        // For simplicity, assume the result is logged and no further action is taken
    }
}