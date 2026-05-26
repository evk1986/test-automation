package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NetconfWorkerHandler {

    private final NetconfRetryService netconfRetryService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public NetconfWorkerHandler(NetconfRetryService netconfRetryService, QueueMessagingTemplate queueMessagingTemplate) {
        this.netconfRetryService = netconfRetryService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        try {
            // Implement NETCONF session timeout handling and retry logic
            netconfRetryService.retryNetconfSession(message);
        } catch (Exception e) {
            // Route exhausted-retry batches to dead-letter queue
            queueMessagingTemplate.convertAndSend("platform.results.dlq", message);
        }
    }
}