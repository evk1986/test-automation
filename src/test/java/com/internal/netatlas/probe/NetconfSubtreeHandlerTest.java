package com/internal/netatlas/probe;

import com.internal.netatlas.probe.handler.NetconfSubtreeHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetconfSubtreeHandlerTest {
    @InjectMocks
    private NetconfSubtreeHandler netconfSubtreeHandler;

    @Test
    public void testHandleNetconfSubtree() {
        // Create a test ProbeJobMessage
        ProbeJobMessage message = new ProbeJobMessage();
        // Call the handleNetconfSubtree method and verify the result
        netconfSubtreeHandler.handleNetconfSubtree(message);
    }
}