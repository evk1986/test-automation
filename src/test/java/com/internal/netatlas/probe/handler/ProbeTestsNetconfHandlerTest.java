package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.ProbeTestsNetconfService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProbeTestsNetconfHandlerTest {

    @Mock
    private ProbeTestsNetconfService netconfService;

    private SimpleMeterRegistry meterRegistry;

    @InjectMocks
    private ProbeTestsNetconfHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry();
        handler = new ProbeTestsNetconfHandler(netconfService, meterRegistry, "probe.commands");
    }

    @Test
    void shouldProcessNetconfJobWhenProtocolIsNetconf() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setJobId("JOB-NETCONF-4821");
        msg.setDeviceId("device-123");
        msg.setProtocol("NETCONF");
        msg.setCredentials("dummy-creds");

        handler.handle(msg);

        verify(netconfService, times(1)).processNetconfJob(msg);
        assertEquals(1, meterRegistry.get("probe.netconf.success").counter().count());
    }

    @Test
    void shouldIgnoreNonNetconfProtocol() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setJobId("JOB-SSH-9999");
        msg.setDeviceId("device-999");
        msg.setProtocol("SSH");
        msg.setCredentials("dummy-creds");

        handler.handle(msg);

        verifyNoInteractions(netconfService);
        assertEquals(0, meterRegistry.get("probe.netconf.success").counter().count());
        assertEquals(0, meterRegistry.get("probe.netconf.failure").counter().count());
    }

    @Test
    void shouldRecordFailureMetricWhenServiceThrows() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setJobId("JOB-NETCONF-FAIL");
        msg.setDeviceId("device-fail");
        msg.setProtocol("NETCONF");
        msg.setCredentials("bad-creds");

        doThrow(new RuntimeException("simulated failure")).when(netconfService).processNetconfJob(msg);

        handler.handle(msg);

        verify(netconfService, times(1)).processNetconfJob(msg);
        assertEquals(1, meterRegistry.get("probe.netconf.failure").counter().count());
    }
}
