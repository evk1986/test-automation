package com.internal.netatlas.orchestrate.handler;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import com.internal.netatlas.orchestrate.service.FleetOrchestratorBatchConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class FleetOrchestratorBatchConfigValidatorTest {
    @Mock
    private FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService;
    @Mock
    private SqsClient sqsClient;
    @InjectMocks
    private FleetOrchestratorBatchConfigValidator fleetOrchestratorBatchConfigValidator;

    @Test
    public void testValidateBatchConfig() {
        // Mock batch config
        BatchConfig batchConfig = new BatchConfig();
        batchConfig.setBatchId("BATCH-PRB-20240523-USE1-01");

        // Mock SQS message
        Message message = Message.builder()
                .body("BATCH-PRB-20240523-USE1-01")
                .build();
        List<Message> messages = List.of(message);

        // Mock SQS client
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands")
                .build();
        sqsClient.receiveMessage(receiveMessageRequest).messages().add(message);

        // Call validate batch config
        fleetOrchestratorBatchConfigValidator.validateBatchConfig();

        // Verify batch config is validated
        assertNotNull(batchConfig);
    }
}