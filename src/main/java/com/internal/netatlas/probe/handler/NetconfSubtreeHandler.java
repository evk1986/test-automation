package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NetconfSubtreeHandler {
    private final QueueMessagingTemplate queueMessagingTemplate;

    public NetconfSubtreeHandler(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void handleNetconfSubtree(ProbeJobMessage message) {
        // Implement NETCONF subtree handling logic for Cisco IOS-XR NCS devices
        // For example, send a NETCONF request to the device and process the response
        queueMessagingTemplate.convertAndSend("probe.commands", message);
    }
}