package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NetconfHandlerTest {
    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    @InjectMocks
    private NetconfHandler netconfHandler;

    @Test
    void testHandle() {
        // Arrange
        ProbeJobMessage message = new ProbeJobMessage("NETCONF", "device-id");

        // Act
        netconfHandler.handle(message);

        // Assert
        verify(queueMessagingTemplate).convertAndSend("normalize.ingest", message);
    }
}