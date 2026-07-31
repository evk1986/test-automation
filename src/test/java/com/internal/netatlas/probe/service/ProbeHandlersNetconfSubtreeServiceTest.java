package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.repository.DeviceSnapshotRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProbeHandlersNetconfSubtreeServiceTest {

    @Mock
    private NetconfAdapter netconfAdapter;

    @Mock
    private DeviceSnapshotRepository snapshotRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter failureCounter;

    private ProbeHandlersNetconfSubtreeService service;

    @BeforeEach
    void setUp() {
        // The service builds the counter via Counter.builder(...).register(meterRegistry).
        // For the test we stub the registry to return our mock counter when the exact metric name and tags are requested.
        when(meterRegistry.counter(eq("probe.protocol.failures"),
                eq("protocol"), eq("NETCONF"),
                eq("region"), eq("us-east-1")))
                .thenReturn(failureCounter);

        service = new ProbeHandlersNetconfSubtreeService(netconfAdapter, snapshotRepository, meterRegistry);
    }

    @Test
    void whenNetconfFails_counterIsIncremented() throws Exception {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setId("JOB-NETCONF-4821");
        msg.setDeviceId("device-123");
        msg.setProtocol("NETCONF");

        // Simulate a failure in the adapter
        doThrow(new RuntimeException("simulated NETCONF failure")).when(netconfAdapter).executeSubtree(msg);

        service.processSubtree(msg);

        // Verify that the failure counter was incremented and no snapshot was persisted
        verify(failureCounter, times(1)).increment();
        verifyNoInteractions(snapshotRepository);
    }

    @Test
    void whenNetconfSucceeds_snapshotIsSaved() throws Exception {
        ProbeJobMessage msg = new ProbeJobMessage();
        msg.setId("JOB-NETCONF-4821");
        msg.setDeviceId("device-123");
        msg.setProtocol("NETCONF");

        when(netconfAdapter.executeSubtree(msg)).thenReturn("<rpc-reply>data</rpc-reply>");

        service.processSubtree(msg);

        // Verify that a DeviceSnapshot was saved and the failure counter was not touched
        verify(snapshotRepository, times(1)).save(any(DeviceSnapshot.class));
        verifyNoInteractions(failureCounter);
    }
}
