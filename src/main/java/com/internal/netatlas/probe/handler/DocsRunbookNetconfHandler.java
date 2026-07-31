package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.DocsRunbookNetconfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class DocsRunbookNetconfHandler {

    private static final Logger log = LoggerFactory.getLogger(DocsRunbookNetconfHandler.class);
    private final DocsRunbookNetconfService netconfService;

    public DocsRunbookNetconfHandler(DocsRunbookNetconfService netconfService) {
        this.netconfService = netconfService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message == null) {
            log.warn("Received null ProbeJobMessage, ignoring");
            return;
        }
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            log.info("Message {} is not NETCONF (protocol={}), skipping", message.getJobId(), message.getProtocol());
            return;
        }
        log.info("Processing NETCONF job {} for device {}", message.getJobId(), message.getDeviceId());
        netconfService.process(message);
    }
}
