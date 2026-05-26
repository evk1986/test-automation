package com.internal.netatlas.probe;

import com.internal.netatlas.probe.handler.NetconfExponentialBackoffHandler;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NetconfExponentialBackoffHandlerTest {

    @InjectMocks
    private NetconfExponentialBackoffHandler handler;

    @Test
    void testHandle() {
        ProbeJobMessage message = new ProbeJobMessage();
        handler.handle(message);
        verify(handler).handle(any(ProbeJobMessage.class));
    }
}