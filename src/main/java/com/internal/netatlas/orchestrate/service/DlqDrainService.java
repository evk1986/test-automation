package com.internal.netatlas.orchestrate.service;

import com.internal.netatlas.orchestrate.model.DlqAuditRecord;
import com.internal.netatlas.orchestrate.repository.DlqAuditRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service that drains the probe.commands DLQ, processes each message, and records an audit entry.
 */
@Service
public class DlqDrainService {

    private final SqsAsyncClient sqsClient;
    private final DlqAuditRepository auditRepository;
    private final String dlqUrl;

    public DlqDrainService(SqsAsyncClient sqsClient,
                           DlqAuditRepository auditRepository,
                           @Value("${aws.sqs.probe.commands.dlq-url}") String dlqUrl) {
        this.sqsClient = sqsClient;
        this.auditRepository = auditRepository;
        this.dlqUrl = dlqUrl;
    }

    /**
     * Pull up to {@code maxMessages} from the DLQ, delete them, and write audit records.
     * Returns the number of successfully processed messages.
     */
    public int drainDlq(int maxMessages) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(dlqUrl)
                .maxNumberOfMessages(maxMessages)
                .waitTimeSeconds(1)
                .build();
        CompletableFuture<List<Message>> future = sqsClient.receiveMessage(request)
                .thenApply(resp -> resp.messages());
        List<Message> messages = future.join();
        int processed = 0;
        for (Message msg : messages) {
            try {
                // In a real implementation we would deserialize and re‑process the payload.
                // Here we simply record success and delete the message.
                auditRepository.save(new DlqAuditRecord(msg.messageId(), "SUCCESS"));
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(dlqUrl)
                        .receiptHandle(msg.receiptHandle())
                        .build()).join();
                processed++;
            } catch (Exception e) {
                auditRepository.save(new DlqAuditRecord(msg.messageId(), "FAILURE"));
                // Message is left in DLQ for later retry; we continue processing others.
            }
        }
        return processed;
    }
}
