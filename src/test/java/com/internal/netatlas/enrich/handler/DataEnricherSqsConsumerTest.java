package com.internal.netatlas.enrich.handler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.enrich.model.EnrichmentResult;
import com.internal.netatlas.enrich.service.DataEnricherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataEnricherSqsConsumerTest {
    @Mock
    private AmazonSQS sqs;
    @Mock
    private DataEnricherService dataEnricherService;
    @InjectMocks
    private DataEnricherSqsConsumer dataEnricherSqsConsumer;

    @Test
    void testConsume() {
        // setup mock data
        Message message = new Message().withBody("messageBody").withMessageAttributes(any());
        when(sqs.receiveMessage(any())).thenReturn(new com.amazonaws.services.sqs.model.ReceiveMessageResult().withMessages(message));
        when(dataEnricherService.isDuplicate(any())).thenReturn(false);
        when(dataEnricherService.enrich(any())).thenReturn(new EnrichmentResult("id", "messageBody"));

        // call method under test
        dataEnricherSqsConsumer.consume();
    }
}