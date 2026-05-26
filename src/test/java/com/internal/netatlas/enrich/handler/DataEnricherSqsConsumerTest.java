package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.AWSSQS;
import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataEnricherSqsConsumerTest {
    @Mock
    private DataEnricherService dataEnricherService;
    @Mock
    private AWSSQS awsSqs;
    @InjectMocks
    private DataEnricherSqsConsumer dataEnricherSqsConsumer;

    @Test
    public void testConsumeMessages() {
        Message message = new Message("messageId", "messageBody");
        when(awsSqs.receiveMessage(new com.amazonaws.services.sqs.model.ReceiveMessageRequest("enrich.pipeline"))).thenReturn(new com.amazonaws.services.sqs.model.ReceiveMessageResult(List.of(message)));
        when(dataEnricherService.isIdempotent(message)).thenReturn(true);
        dataEnricherSqsConsumer.consumeMessages();
    }
}