package com.internal.netatlas.orchestrate.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;

@Service
public class DLQDrainService {

    private final SqsClient sqsClient;
    private final String dlqUrl;
    private final String mainQueueUrl;
    private final MeterRegistry meterRegistry;
    private final Timer drainTimer;

    public DLQDrainService(SqsClient sqsClient,
                           @Value("${aws.sqs.dlq.probe-commands.url}") String dlqUrl,
                           @Value("${aws.sqs.probe-commands.url}") String mainQueueUrl,
                           MeterRegistry meterRegistry) {
        this.sqsClient = sqsClient;
        this.dlqUrl = dlqUrl;
        this.mainQueueUrl = mainQueueUrl;
        this.meterRegistry = meterRegistry;
        this.drainTimer = meterRegistry.timer("platform.dlq.probe_commands.drain");
    }

    public int drainDlq(int maxMessages) {
        return drainTimer.record(() -> {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(dlqUrl)
                    .maxNumberOfMessages(Math.min(maxMessages, 10))
                    .waitTimeSeconds(0)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            int processed = 0;
            for (Message msg : messages) {
                // Re‑publish to main queue for retry
                SendMessageRequest sendRequest = SendMessageRequest.builder()
                        .queueUrl(mainQueueUrl)
                        .messageBody(msg.body())
                        .build();
                sqsClient.sendMessage(sendRequest);

                // Remove from DLQ
                DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(dlqUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build();
                sqsClient.deleteMessage(deleteRequest);
                processed++;
            }
            meterRegistry.counter("platform.dlq.probe_commands.processed", "batch", "drain").increment(processed);
            return processed;
        });
    }
}
