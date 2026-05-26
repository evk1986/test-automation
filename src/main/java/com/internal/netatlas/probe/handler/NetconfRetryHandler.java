package com.internal.netatlas.probe.handler;

import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NetconfRetryHandler {
    private final NetconfBatchRetryService netconfBatchRetryService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public NetconfRetryHandler(NetconfBatchRetryService netconfBatchRetryService, QueueMessagingTemplate queueMessagingTemplate) {
        this.netconfBatchRetryService = netconfBatchRetryService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        try {
            // Implement exponential-backoff retry
            netconfBatchRetryService.retryFailedJobs(message.getBatchId());
        } catch (Exception e) {
            // Route exhausted-retry batches to dead-letter queue
            queueMessagingTemplate.convertAndSend("platform.results.dlq", message);
        }
    }
}