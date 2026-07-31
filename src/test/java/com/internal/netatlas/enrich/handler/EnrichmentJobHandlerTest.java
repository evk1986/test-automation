package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.model.EnrichmentJobMessage;
import com.internal.netatlas.enrich.service.EnrichmentJobService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sqs.SqsClient;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EnrichmentJobHandlerTest {

    @Mock
    private EnrichmentJobService enrichmentJobService;

    @Mock
    private SqsClient sqsClient;

    private SimpleMeterRegistry meterRegistry;

    @InjectMocks
    private EnrichmentJobHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        meterRegistry = new SimpleMeterRegistry();
        handler = new EnrichmentJobHandler(enrichmentJobService, meterRegistry, sqsClient, "https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline");
    }

    @Test
    void shouldProcessMessageAndDeleteOnSuccess() {
        EnrichmentJobMessage msg = new EnrichmentJobMessage();
        msg.setProtocol("NETCONF");
        msg.setRegion("us-east-1");
        String receiptHandle = "rh-123";
        String messageId = "msg-001";

        // No exception from service → happy path
        doNothing().when(enrichmentJobService).process(msg, receiptHandle, messageId);

        handler.handle(msg, receiptHandle, messageId);

        verify(enrichmentJobService, times(1)).process(msg, receiptHandle, messageId);
        // Verify that the deleteMessage call was performed
        verify(sqsClient, times(1)).deleteMessage(any());
        // No failure metric should be recorded
        assertEquals(0, meterRegistry.get("enricher.failures").counter().count());
    }

    @Test
    void shouldIncrementFailureCounterWhenProcessingThrows() {
        EnrichmentJobMessage msg = new EnrichmentJobMessage();
        msg.setProtocol("SNMP");
        msg.setRegion("us-west-2");
        String receiptHandle = "rh-456";
        String messageId = "msg-002";

        doThrow(new RuntimeException("boom")).when(enrichmentJobService).process(msg, receiptHandle, messageId);

        handler.handle(msg, receiptHandle, messageId);

        verify(enrichmentJobService, times(1)).process(msg, receiptHandle, messageId);
        // Delete should NOT be called because processing failed
        verify(sqsClient, never()).deleteMessage(any());
        // Failure counter should be incremented with correct tags
        assertEquals(1, meterRegistry.get("enricher.failures").tags("protocol", "SNMP", "region", "us-west-2").counter().count());
    }
}
