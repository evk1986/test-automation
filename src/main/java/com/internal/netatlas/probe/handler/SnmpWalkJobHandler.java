package com.internal.netatlas.probe.handler;

import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SnmpWalkJobHandler {
    private final NetconfBatchRetryService netconfBatchRetryService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public SnmpWalkJobHandler(NetconfBatchRetryService netconfBatchRetryService, QueueMessagingTemplate queueMessagingTemplate) {
        this.netconfBatchRetryService = netconfBatchRetryService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Validate the message
        if (message.getProtocol().equals("SNMP")) {
            // Delegate to processor
            netconfBatchRetryService.retryFailedJobs(message.getBatchId());
            // Send result to SNS
            queueMessagingTemplate.convertAndSend("enrich.pipeline", message);
        }
    }
}