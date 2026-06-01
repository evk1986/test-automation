package com.internal.netatlas.probe.handler;

import com.amazonaws.services.sqs.model.Message;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class P008SingleDaySmokeHandler {
    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Validate the message and delegate to the processor
        if (message.getProtocol().equals("NETCONF")) {
            // Process the message using the NETCONF protocol
            System.out.println("Processing message using NETCONF protocol");
        } else {
            // Process the message using another protocol
            System.out.println("Processing message using another protocol");
        }
    }
}