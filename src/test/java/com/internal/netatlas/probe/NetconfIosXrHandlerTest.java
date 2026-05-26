package com/internal/netatlas/probe;

import com.internal.netatlas.probe.handler.NetconfIosXrHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NetconfIosXrHandlerTest {
    @InjectMocks
    private NetconfIosXrHandler handler;

    @Test
    public void testHandle() {
        // Test the handle method of the NetconfIosXrHandler class
        ProbeJobMessage message = new ProbeJobMessage();
        handler.handle(message);
    }
}