package com.internal.netatlas.probe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.internal.netatlas.probe.handler.NetconfJobProcessor;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NetconfDlqReplayService {

    private static final String DLQ_QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/123456789012/platform.results.dlq";
    private final AmazonSQS sqsClient;
    private final ProbeJobRepository jobRepository;
    private final NetconfJobProcessor processor;
    private final HazelcastInstance hazelcastInstance;
    private final Counter replayCounter;

    public NetconfDlqReplayService(AmazonSQS sqsClient,
                                   ProbeJobRepository jobRepository,
                                   NetconfJobProcessor processor,
                                   HazelcastInstance hazelcastInstance,
                                   MeterRegistry meterRegistry) {
        this.sqsClient = sqsClient;
        this.jobRepository = jobRepository;
        this.processor = processor;
        this.hazelcastInstance = hazelcastInstance;
        this.replayCounter = Counter.builder("netconf.dlq.replay.processed")
                .description("Number of DLQ messages replayed for NETCONF")
                .register(meterRegistry);
    }

    public void replayFailedMessages() {
        ReceiveMessageRequest request = new ReceiveMessageRequest()
                .withQueueUrl(DLQ_QUEUE_URL)
                .withMaxNumberOfMessages(10)
                .withWaitTimeSeconds(5);
        List<Message> messages = sqsClient.receiveMessage(request).getMessages();
        for (Message msg : messages) {
            try {
                ProbeJob job = new ObjectMapper().readValue(msg.getBody(), ProbeJob.class);
                // Skip if job already succeeded
                if (job.getStatus() != null && job.getStatus().name().equals("SUCCESS")) {
                    deleteMessage(msg);
                    continue;
                }
                String lockName = "netconf-lock-" + job.getDeviceId();
                FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(lockName);
                if (lock.tryLock()) {
                    try {
                        processor.process(job);
                        replayCounter.increment();
                    } finally {
                        lock.unlock();
                    }
                } else {
                    // Could not acquire lock, will retry later
                }
                deleteMessage(msg);
            } catch (Exception e) {
                // Log and leave message in DLQ for future attempts
                System.err.println("Failed to replay DLQ message: " + e.getMessage());
            }
        }
    }

    private void deleteMessage(Message msg) {
        sqsClient.deleteMessage(DLQ_QUEUE_URL, msg.getReceiptHandle());
    }
}
