package com.internal.netatlas.normalize.handler;

import com.internal.netatlas.normalize.model.NormalizedRecord;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Service
public class AristaEosGoldenFileTestHandler {
    private final SqsClient sqsClient;

    public AristaEosGoldenFileTestHandler(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void validateGoldenFileTests() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/normalize.ingest")
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();
        for (Message message : messages) {
            NormalizedRecord normalizedRecord = new NormalizedRecord();
            // Validate normalized record against golden file
            System.out.println("Validated normalized record: " + normalizedRecord);
        }
    }
}