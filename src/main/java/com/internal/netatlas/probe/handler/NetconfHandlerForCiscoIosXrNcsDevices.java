package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NetconfHandlerForCiscoIosXrNcsDevices {
    private final QueueMessagingTemplate queueMessagingTemplate;

    public NetconfHandlerForCiscoIosXrNcsDevices(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handleNetconfMessage(ProbeJobMessage message) {
        // Validate the message and delegate to the processor
        if (message.getProtocol().equals("NETCONF") && message.getDeviceFamily().equals("Cisco IOS-XR NCS")) {
            // Process the NETCONF message for Cisco IOS-XR NCS devices
            queueMessagingTemplate.convertAndSend("enrich.pipeline", message);
        }
    }
}