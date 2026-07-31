package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.EnricherProcessingService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ComTelecomPipelineEnricherConsumer}.
 */
class ComTelecomPipelineEnricherConsumerTest {

    @Mock
    private EnricherProcessingService processingService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter successCounter;

    @Mock
    private Counter failureCounter;

    @InjectMocks
    private ComTelecomPipelineEnricherConsumer consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(meterRegistry.counter(eq("enricher.idempotency.success"), anyString(), anyString()))
                .thenReturn(successCounter);
        when(meterRegistry.counter(eq("enricher.idempotency.failures"), anyString(), anyString()))
                .thenReturn(failureCounter);
    }

    @Test
    void handle_NewMessage_ProcessesAndIncrementsSuccess() {
        // Arrange
        EnrichmentMessage payload = new EnrichmentMessage();
        payload.setProtocol("NETCONF");
        payload.setRegion("us-east-1");
        Map<String, String> headers = new HashMap<>();
        headers.put("MessageId", "msg-12345");
        headers.put("ReceiptHandle", "rh-abcde");

        // Act
        consumer.handle(payload, headers);

        // Assert
        verify(processingService, times(1)).process(eq(payload), eq("msg-12345"), eq("rh-abcde"), anyInt());
        verify(successCounter, times(1)).increment();
        verifyNoInteractions(failureCounter);
    }

    @Test
    void handle_DuplicateMessage_ServiceSkipsProcessing_NoSuccessIncrement() {
        // Arrange – make the service throw an IllegalStateException to simulate duplicate handling
        EnrichmentMessage payload = new EnrichmentMessage();
        payload.setProtocol("SNMP");
        payload.setRegion("us-west-2");
        Map<String, String> headers = new HashMap<>();
        headers.put("MessageId", "msg-dup-001");
        headers.put("ReceiptHandle", "rh-dup-001");

        doThrow(new IllegalStateException("Duplicate message"))
                .when(processingService).process(any(), anyString(), anyString(), anyInt());

        // Act & Assert – exception propagates and failure counter is incremented
        Exception ex = assertThrows(IllegalStateException.class, () -> consumer.handle(payload, headers));
        assertEquals("Duplicate message", ex.getMessage());
        verify(failureCounter, times(1)).increment();
        verifyNoInteractions(successCounter);
    }
}
