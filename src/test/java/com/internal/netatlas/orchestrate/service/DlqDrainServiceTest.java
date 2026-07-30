package com.internal.netatlas.orchestrate.service;

import com.internal.netatlas.orchestrate.model.DlqAuditRecord;
import com.internal.netatlas.orchestrate.repository.DlqAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DlqDrainServiceTest {

    @Mock
    private SqsAsyncClient sqsClient;
    @Mock
    private DlqAuditRepository auditRepository;

    private DlqDrainService drainService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        drainService = new DlqDrainService(sqsClient, auditRepository, "http://dummy-dlq-url");
    }

    @Test
    void shouldProcessAndDeleteMessagesFromDlq() {
        Message mockMsg = Message.builder()
                .messageId("msg-123")
                .receiptHandle("rh-123")
                .body("{\"jobId\":\"BATCH-PRB-20240523-USE1-01\"}")
                .build();
        ReceiveMessageResponse resp = ReceiveMessageResponse.builder()
                .messages(mockMsg)
                .build();
        when(sqsClient.receiveMessage(any()))
                .thenReturn(CompletableFuture.completedFuture(resp));
        when(sqsClient.deleteMessage(any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        int processed = drainService.drainDlq(5);
        assertEquals(1, processed);

        ArgumentCaptor<DlqAuditRecord> auditCaptor = ArgumentCaptor.forClass(DlqAuditRecord.class);
        verify(auditRepository, times(1)).save(auditCaptor.capture());
        DlqAuditRecord saved = auditCaptor.getValue();
        assertEquals("msg-123", saved.getMessageId());
        assertEquals("SUCCESS", saved.getStatus());
        verify(sqsClient, times(1)).deleteMessage(any());
    }
}
