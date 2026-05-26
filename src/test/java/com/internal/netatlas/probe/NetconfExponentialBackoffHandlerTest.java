package com.internal.netatlas.probe;

import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.probe.handler.NetconfExponentialBackoffHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfExponentialBackoffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NetconfExponentialBackoffHandlerTest {

    @Mock
    private NetconfExponentialBackoffService netconfExponentialBackoffService;

    @InjectMocks
    private NetconfExponentialBackoffHandler handler;

    @Test
    public void testHandle() {
        ProbeJobMessage message = new ProbeJobMessage();
        handler.handle(message);
        verify(netconfExponentialBackoffService).retryWithExponentialBackoff(any(ProbeJobMessage.class));
    }
}