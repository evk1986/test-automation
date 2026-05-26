package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NetconfHandler {
    private final QueueMessagingTemplate queueMessagingTemplate;

    public NetconfHandler(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Validate and delegate to processor
        if (message.getProtocol().equals("NETCONF")) {
            // Process NETCONF message
            queueMessagingTemplate.convertAndSend("normalize.ingest", message);
        }
    }
}