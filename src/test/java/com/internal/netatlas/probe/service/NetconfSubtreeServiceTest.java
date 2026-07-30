package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.repository.DeviceSnapshotRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link NetconfSubtreeService} verifying that the failure counter is
 * incremented when the NETCONF adapter throws an exception.
 */
class NetconfSubtreeServiceTest {

    @Test
    void incrementsFailureCounterOnAdapterException() {
        // Arrange
        NetconfAdapter mockAdapter = mock(NetconfAdapter.class);
        DeviceSnapshotRepository mockRepo = mock(DeviceSnapshotRepository.class);
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

        NetconfSubtreeService service = new NetconfSubtreeService(mockAdapter, mockRepo, meterRegistry);

        ProbeJobMessage message = new ProbeJobMessage();
        message.setProtocol("NETCONF");
        message.setDeviceId("ncs-001");
        message.setJobId("JOB-NETCONF-4821");

        when(mockAdapter.executeSubtree(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Netconf error"));

        // Act
        service.dispatchSubtree(message);

        // Assert – the counter should have a count of 1.
        double count = meterRegistry.get("probe.protocol.failures")
                .tag("protocol", "netconf")
                .tag("region", "us-east-1")
                .counter()
                .count();
        assertEquals(1.0, count, "Failure counter should be incremented once");
        // Ensure no snapshot was persisted.
        verifyNoInteractions(mockRepo);
    }
}
