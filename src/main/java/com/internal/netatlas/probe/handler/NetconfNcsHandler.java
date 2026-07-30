package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfNcsProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NetconfNcsHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfNcsHandler.class);
    private final NetconfNcsProcessingService processingService;

    @Autowired
    public NetconfNcsHandler(NetconfNcsProcessingService processingService) {
        this.processingService = processingService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message == null) {
            LOG.warn("Received null ProbeJobMessage");
            return;
        }
        // Filter only NETCONF jobs targeting Cisco IOS-XR NCS devices
        if ("NETCONF".equalsIgnoreCase(message.getProtocol()) && "IOS-XR_NCS".equalsIgnoreCase(message.getDeviceFamily())) {
            LOG.info("Processing NETCONF job {} for device {}", message.getJobId(), message.getDeviceId());
            processingService.process(message);
        } else {
            LOG.debug("Skipping non‑NETCONF or unsupported device family: {} / {}", message.getProtocol(), message.getDeviceFamily());
        }
    }
}
