package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class NetconfSessionRetryHandler {
    private final NetconfAdapter netconfAdapter;
    private final SqsClient sqsClient;

    @Autowired
    public NetconfSessionRetryHandler(NetconfAdapter netconfAdapter, SqsClient sqsClient) {
        this.netconfAdapter = netconfAdapter;
        this.sqsClient = sqsClient;
    }

    public void handle(ProbeJob probeJob) {
        // Implement exponential backoff retry strategy for NETCONF session timeout
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                netconfAdapter.executeNetconfCommand(probeJob);
                break;
            } catch (Exception e) {
                retryCount++;
                // Calculate exponential backoff delay
                long delay = (long) Math.pow(2, retryCount) * 1000;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (retryCount >= 5) {
            // Route exhausted-retry batches to dead-letter queue
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl("https://sqs.us-east-1.amazonaws.com/123456789012/probe.commands.dlq")
                    .messageBody(probeJob.toString())
                    .build();
            sqsClient.sendMessage(sendMessageRequest);
        }
    }
}