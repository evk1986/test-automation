package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfSubtreeService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProbeHandlersNetconfJobDispatcherTest {

    private NetconfSubtreeService mockService;
    private SimpleMeterRegistry meterRegistry;
    private ProbeHandlersNetconfJobDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        mockService = mock(NetconfSubtreeService.class);
        meterRegistry = new SimpleMeterRegistry();
        dispatcher = new ProbeHandlersNetconfJobDispatcher(mockService, meterRegistry);
    }

    @Test
    void shouldDispatchNetconfMessageAndIncrementCounter() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("NETCONF");
        msg.setJobId("JOB-NETCONF-4821");
        msg.setDeviceId("device-123");

        dispatcher.handle(msg);

        verify(mockService, times(1)).processSubtreeJob(msg);
        double count = meterRegistry.get("netconf.subtree.dispatch").counter().count();
        assertEquals(1.0, count, "Counter should be incremented once");
    }

    @Test
    void shouldIgnoreNonNetconfMessage() {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setProtocol("SNMP");
        dispatcher.handle(msg);
        verifyNoInteractions(mockService);
        double count = meterRegistry.get("netconf.subtree.dispatch").counter().count();
        assertEquals(0.0, count, "Counter should not increment for non-NETCONF");
    }
}
