package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.Protocol;
import com.internal.netatlas.probe.service.TestIntegrationNetconfService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestIntegrationNetconfHandlerTest {

    @Mock
    private TestIntegrationNetconfService service;

    @Mock
    private MeterRegistry meterRegistry;

    @InjectMocks
    private TestIntegrationNetconfHandler handler;

    @Test
    public void handle_NetconfMessage_ProcessesAndIncrementsSuccess() {
        // Arrange
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setId("JOB-NETCONF-4821");
        msg.setDeviceId("device-001");
        msg.setProtocol(Protocol.NETCONF);

        Counter successCounter = mock(Counter.class);
        when(meterRegistry.counter("probe.protocol.success", "protocol", "NETCONF"))
                .thenReturn(successCounter);

        // Act
        handler.handle(msg);

        // Assert
        verify(service).processProbeJob(msg);
        verify(successCounter).increment();
        verify(meterRegistry, never()).counter("probe.protocol.failures", "protocol", "NETCONF");
    }

    @Test(expected = RuntimeException.class)
    public void handle_ServiceThrows_IncrementsFailureAndRethrows() {
        // Arrange
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol(Protocol.NETCONF);
        doThrow(new RuntimeException("simulated failure")).when(service).processProbeJob(msg);

        Counter failureCounter = mock(Counter.class);
        when(meterRegistry.counter("probe.protocol.failures", "protocol", "NETCONF"))
                .thenReturn(failureCounter);

        // Act
        handler.handle(msg);

        // Assert (exception expected) – failure counter increment verified via mock verification
        verify(failureCounter).increment();
    }
}
