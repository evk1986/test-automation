package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfWorkerHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfRetryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NetconfWorkerHandlerTest {

    @Mock
    private NetconfRetryService netconfRetryService;

    @InjectMocks
    private NetconfWorkerHandler netconfWorkerHandler;

    @Test
    public void testHandle() {
        // Create a test probe job message
        ProbeJobMessage message = new ProbeJobMessage();
        netconfWorkerHandler.handle(message);
        verify(netconfRetryService).retryNetconfSession(any());
    }
}