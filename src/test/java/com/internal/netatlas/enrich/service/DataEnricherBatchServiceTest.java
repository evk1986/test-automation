package com.internal.netatlas.enrich.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.internal.netatlas.enrich.model.EnrichmentMessage;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.repository.EnrichmentResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataEnricherBatchServiceTest {

    @Mock
    private EnrichmentResultRepository repository;

    @Mock
    private AmazonSQS sqsClient;

    @InjectMocks
    private DataEnricherBatchService service;

    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/enrich.pipeline";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject the queue URL via reflection because the service reads it from @Value.
        // In a real test we would use @TestPropertySource, but for this unit test a simple setter works.
        service = new DataEnricherBatchService(repository, sqsClient, queueUrl);
    }

    @Test
    void shouldPersistOnlyOnceWhenDuplicateMessagesArrive() {
        // Arrange – first call sees no existing record, second call sees the record.
        EnrichmentMessage msg1 = new EnrichmentMessage("msg-001", "rh-111", "norm-123",
                Collections.singletonMap("fieldA", "valueA"), "arn:aws:sns:us-east-1:123456789012:enrich-output");
        EnrichmentMessage msg2 = new EnrichmentMessage("msg-001", "rh-222", "norm-123",
                Collections.singletonMap("fieldA", "valueA"), "arn:aws:sns:us-east-1:123456789012:enrich-output");

        when(repository.findById("msg-001"))
                .thenReturn(Optional.empty())   // first lookup – not present
                .thenReturn(Optional.of(new EnrichmentResult())); // second lookup – present

        // Act – process first message
        service.processMessage(msg1);
        // Act – process duplicate message
        service.processMessage(msg2);

        // Assert – repository.save called exactly once
        verify(repository, times(1)).save(any(EnrichmentResult.class));
        // Assert – visibility timeout extended for both messages
        verify(sqsClient, times(2)).changeMessageVisibility(any(ChangeMessageVisibilityRequest.class));
    }

    @Test
    void shouldPopulateEnrichmentResultFieldsCorrectly() {
        EnrichmentMessage msg = new EnrichmentMessage("msg-002", "rh-333", "norm-456",
                Collections.singletonMap("speed", "10Gbps"), "arn:aws:sns:us-east-1:123456789012:enrich-output");
        when(repository.findById("msg-002")).thenReturn(Optional.empty());

        service.processMessage(msg);

        ArgumentCaptor<EnrichmentResult> captor = ArgumentCaptor.forClass(EnrichmentResult.class);
        verify(repository).save(captor.capture());
        EnrichmentResult saved = captor.getValue();
        assertEquals("msg-002", saved.getMessageId());
        assertEquals("norm-456", saved.getNormalizedRecordId());
        assertEquals("arn:aws:sns:us-east-1:123456789012:enrich-output", saved.getDownstreamTopicArn());
        assertNotNull(saved.getEnrichedAt());
        assertEquals("10Gbps", saved.getEnrichedFields().get("speed"));
    }
}
