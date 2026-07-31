package com.internal.netatlas.enrich.handler;

import com.internal.netatlas.enrich.service.EnrichmentProcessingService;
import com.internal.netatlas.enrich.service.EnrichmentProcessingService.EnrichmentResultRepository;
import com.internal.netatlas.enrich.handler.EnrichPipelineConsumer.EnrichmentMessage;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnricherConsumerTests {

    @Mock
    private EnrichmentResultRepository repository;

    @InjectMocks
    private EnrichmentProcessingService service;

    @Test
    void testIdempotentProcessing() {
        // Arrange – first call sees no existing record, second call sees the record.
        EnrichmentMessage msg = new EnrichmentMessage("device-123", "norm-abc", "{\"key\":\"value\"}");
        when(repository.findById("norm-abc"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new EnrichmentProcessingService.EnrichmentResult()));

        // Act – invoke twice to simulate duplicate SQS delivery.
        service.process(msg);
        service.process(msg);

        // Assert – repository.save should be called exactly once.
        verify(repository, times(1)).save(any(EnrichmentProcessingService.EnrichmentResult.class));
    }

    @Test
    void testMetricEmission() {
        // Use a real SimpleMeterRegistry to verify the counter value.
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
        EnrichmentProcessingService realService = new EnrichmentProcessingService(repository, meterRegistry);

        EnrichmentMessage msg = new EnrichmentMessage("device-456", "norm-def", "{\"foo\":\"bar\"}");
        when(repository.findById("norm-def")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(null);

        realService.process(msg);

        double count = meterRegistry.get("enricher.results.written").counter().count();
        assert count == 1.0;
    }
}
