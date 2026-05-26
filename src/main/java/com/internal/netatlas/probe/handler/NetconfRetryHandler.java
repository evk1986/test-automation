package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NetconfRetryHandler {
    private final NetconfRetryService netconfRetryService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public NetconfRetryHandler(NetconfRetryService netconfRetryService, QueueMessagingTemplate queueMessagingTemplate) {
        this.netconfRetryService = netconfRetryService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message.getProtocol().equals("NETCONF") && message.getDeviceFamily().equals("IOS-XR")) {
            netconfRetryService.retry(message);
        }
    }
}