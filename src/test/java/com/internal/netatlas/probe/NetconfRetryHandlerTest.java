package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfRetryHandler;
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
public class NetconfRetryHandlerTest {

    @Mock
    private NetconfRetryService netconfRetryService;

    @InjectMocks
    private NetconfRetryHandler netconfRetryHandler;

    @Test
    void testHandle() {
        ProbeJobMessage message = new ProbeJobMessage();
        message.setProtocol("NETCONF");
        message.setDeviceFamily("IOS-XR");
        netconfRetryHandler.handle(message);
        verify(netconfRetryService).retry(any());
    }
}