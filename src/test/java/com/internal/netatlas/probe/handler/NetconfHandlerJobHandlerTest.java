package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.NetconfJobMessage;
import com.internal.netatlas.probe.service.NetconfHandlerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class NetconfHandlerJobHandlerTest {

    @Mock
    private NetconfHandlerService netconfHandlerService;

    @InjectMocks
    private NetconfHandlerJobHandler handler;

    @Test
    void shouldDelegateMessageToService() {
        NetconfJobMessage message = new NetconfJobMessage("JOB-NETCONF-4821", "device-123", "NETCONF");
        handler.handle(message);
        verify(netconfHandlerService, times(1)).process(message);
    }
}
