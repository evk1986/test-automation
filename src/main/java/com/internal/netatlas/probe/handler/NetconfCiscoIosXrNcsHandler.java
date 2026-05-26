package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NetconfCiscoIosXrNcsHandler {
    private final NetconfAdapter netconfAdapter;
    private final QueueMessagingTemplate queueMessagingTemplate;
    private final NetconfBatchRetryService netconfBatchRetryService;

    @Autowired
    public NetconfCiscoIosXrNcsHandler(NetconfAdapter netconfAdapter, QueueMessagingTemplate queueMessagingTemplate, NetconfBatchRetryService netconfBatchRetryService) {
        this.netconfAdapter = netconfAdapter;
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.netconfBatchRetryService = netconfBatchRetryService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Connect to Cisco IOS-XR NCS device using NETCONF
        netconfAdapter.connect(message.getDeviceId(), message.getProtocol());
        // Collect and process NETCONF responses
        String response = netconfAdapter.collectResponse(message.getDeviceId(), message.getProtocol());
        // Handle errors and exceptions
        if (response == null) {
            netconfBatchRetryService.retryFailedJobs(message.getBatchId());
        } else {
            queueMessagingTemplate.convertAndSend("normalize.ingest", response);
        }
    }
}