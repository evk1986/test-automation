package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CiscoIosXeNetconfSubtreeServiceTest {

    @Mock
    private NetconfAdapter netconfAdapter;

    @Mock
    private QueueMessagingTemplate queueMessagingTemplate;

    private CiscoIosXeNetconfSubtreeService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CiscoIosXeNetconfSubtreeService(netconfAdapter, queueMessagingTemplate);
    }

    @Test
    public void testCollectSubtreeSuccess() {
        ProbeJob job = new ProbeJob();
        job.setId("BATCH-PRB-20240523-USE1-01");
        job.setDeviceId("device-123");
        job.setProtocol("NETCONF");

        String mockResponse = "<rpc-reply><data>interface-config</data></rpc-reply>";
        when(netconfAdapter.executeGet(eq("device-123"), anyString())).thenReturn(mockResponse);

        DeviceSnapshot snapshot = service.collectSubtree(job);

        assertNotNull(snapshot);
        assertEquals("device-123", snapshot.getDeviceId());
        assertEquals("NETCONF", snapshot.getProtocol());
        assertEquals(mockResponse, snapshot.getRawPayload());

        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(queueMessagingTemplate, times(1)).convertAndSend(queueCaptor.capture(), payloadCaptor.capture());
        assertEquals("probe.commands", queueCaptor.getValue());
        assertTrue(payloadCaptor.getValue().contains("\"jobId\":\"BATCH-PRB-20240523-USE1-01\""));
    }

    @Test
    public void testCollectSubtreeCircuitBreakerOpensAfterFailures() {
        ProbeJob job = new ProbeJob();
        job.setId("JOB-NETCONF-4821");
        job.setDeviceId("device-fail");
        job.setProtocol("NETCONF");

        when(netconfAdapter.executeGet(eq("device-fail"), anyString()))
                .thenThrow(new RuntimeException("Connection error"));

        // First three attempts are retried; after that the circuit breaker should open.
        for (int i = 0; i < 4; i++) {
            try {
                service.collectSubtree(job);
                fail("Expected RuntimeException on attempt " + i);
            } catch (RuntimeException ignored) {
                // Expected
            }
        }

        // Subsequent call should fail fast because the circuit breaker is open.
        assertThrows(RuntimeException.class, () -> service.collectSubtree(job));
    }
}
