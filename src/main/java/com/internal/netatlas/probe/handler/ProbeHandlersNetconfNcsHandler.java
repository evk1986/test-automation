package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfNcsProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQS listener that receives NETCONF probe jobs for Cisco IOS‑XR NCS devices.
 * Delegates the actual processing to {@link NetconfNcsProcessingService}.
 */
@Service
public class ProbeHandlersNetconfNcsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProbeHandlersNetconfNcsHandler.class);

    private final NetconfNcsProcessingService processingService;

    @Autowired
    public ProbeHandlersNetconfNcsHandler(NetconfNcsProcessingService processingService) {
        this.processingService = processingService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        LOG.info("Received NETCONF NCS probe job {} for device {}", message.getJobId(), message.getDeviceId());
        processingService.process(message);
    }
}
