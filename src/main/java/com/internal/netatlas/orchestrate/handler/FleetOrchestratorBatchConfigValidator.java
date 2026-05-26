package com.internal.netatlas.orchestrate.handler;

import com.internal.netatlas.orchestrate.model.BatchConfig;
import com.internal.netatlas.orchestrate.service.FleetOrchestratorBatchConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Service
public class FleetOrchestratorBatchConfigValidator {
    private final FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService;
    private final SqsClient sqsClient;

    @Autowired
    public FleetOrchestratorBatchConfigValidator(FleetOrchestratorBatchConfigService fleetOrchestratorBatchConfigService, SqsClient sqsClient) {
        this.fleetOrchestratorBatchConfigService = fleetOrchestratorBatchConfigService;
        this.sqsClient = sqsClient;
    }

    public void validateBatchConfig() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands")
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        for (Message message : messages) {
            BatchConfig batchConfig = fleetOrchestratorBatchConfigService.getBatchConfig(message.body());
            if (batchConfig != null) {
                // Validate batch config
                System.out.println("Batch config is valid");
            } else {
                System.out.println("Batch config is invalid");
            }
        }
    }
}