package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.DocsRunbooksDocsAdrService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocsRunbooksDocsAdrHandlerTest {

    @Mock
    private DocsRunbooksDocsAdrService service;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter successCounter;

    @Mock
    private Counter failureCounter;

    @InjectMocks
    private DocsRunbooksDocsAdrHandler handler;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter("netconf.handler.success")).thenReturn(successCounter);
        when(meterRegistry.counter("netconf.handler.failure")).thenReturn(failureCounter);
    }

    @Test
    void shouldProcessNetconfMessage() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setJobId("JOB-NETCONF-4821");
        msg.setDeviceId("device-001");
        msg.setProtocol("NETCONF");

        handler.handle(msg);

        verify(service, times(1)).processNetconfJob(msg);
        verify(successCounter, times(1)).increment();
        verifyNoInteractions(failureCounter);
    }

    @Test
    void shouldIgnoreNonNetconfMessage() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("SNMP");

        handler.handle(msg);

        verifyNoInteractions(service);
        verifyNoInteractions(successCounter);
        verifyNoInteractions(failureCounter);
    }

    @Test
    void shouldIncrementFailureCounterWhenServiceThrows() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setJobId("JOB-NETCONF-4821");
        msg.setDeviceId("device-001");
        msg.setProtocol("NETCONF");

        doThrow(new RuntimeException("simulated failure")).when(service).processNetconfJob(any());

        assertThrows(RuntimeException.class, () -> handler.handle(msg));
        verify(failureCounter, times(1)).increment();
        verifyNoInteractions(successCounter);
    }
}
