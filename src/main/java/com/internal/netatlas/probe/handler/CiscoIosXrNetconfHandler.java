package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class CiscoIosXrNetconfHandler {
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public CiscoIosXrNetconfHandler(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Connect to Cisco IOS-XR device using NETCONF protocol
        // Run protocol commands and collect raw responses
        String rawResponse = "Raw response from Cisco IOS-XR device";
        // Publish raw response to SQS queue
        queueMessagingTemplate.convertAndSend("normalize.ingest", rawResponse);
    }
}