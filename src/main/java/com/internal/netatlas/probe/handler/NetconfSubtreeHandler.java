package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NetconfSubtreeHandler {
    private static final Logger LOGGER = Logger.getLogger(NetconfSubtreeHandler.class.getName());

    private final NetconfAdapter netconfAdapter;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final NetconfBatchRetryService netconfBatchRetryService;

    @Autowired
    public NetconfSubtreeHandler(NetconfAdapter netconfAdapter, QueueMessagingTemplate queueMessagingTemplate, NetconfBatchRetryService netconfBatchRetryService) {
        this.netconfAdapter = netconfAdapter;
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.netconfBatchRetryService = netconfBatchRetryService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Validate the message and extract the NETCONF subtree request
        if (message.getProtocol().equals("NETCONF") && message.getDeviceFamily().equals("Cisco IOS-XR NCS")) {
            // Use the NetconfAdapter to send the subtree request and collect the raw response
            String rawResponse = netconfAdapter.sendSubtreeRequest(message.getDeviceId(), message.getProtocol());
            // Process the raw response and update the job status
            netconfBatchRetryService.updateJobStatus(message.getJobId(), rawResponse);
        }
    }
}