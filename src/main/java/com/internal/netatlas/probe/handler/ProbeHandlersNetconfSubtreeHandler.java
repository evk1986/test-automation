package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.ProbeHandlersNetconfSubtreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQS listener that processes NETCONF subtree requests for Cisco IOS‑XR NCS devices.
 * Consumes messages from the {@code probe.commands} queue, validates the protocol and device family,
 * and delegates the actual processing to {@link ProbeHandlersNetconfSubtreeService}.
 */
@Service
public class ProbeHandlersNetconfSubtreeHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProbeHandlersNetconfSubtreeHandler.class);
    private static final String TARGET_FAMILY = "IOS-XR-NCS"; // internal shorthand for Cisco IOS‑XR NCS

    private final ProbeHandlersNetconfSubtreeService subtreeService;

    @Autowired
    public ProbeHandlersNetconfSubtreeHandler(ProbeHandlersNetconfSubtreeService subtreeService) {
        this.subtreeService = subtreeService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        LOG.info("Received ProbeJobMessage id={}, deviceId={}, protocol={}",
                message.getJobId(), message.getDeviceId(), message.getProtocol());

        // Guard: only process NETCONF jobs for the Cisco IOS‑XR NCS family
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            LOG.debug("Skipping message {} because protocol is not NETCONF", message.getJobId());
            return;
        }
        if (!TARGET_FAMILY.equalsIgnoreCase(message.getDeviceFamily())) {
            LOG.debug("Skipping message {} because device family {} is not {}",
                    message.getJobId(), message.getDeviceFamily(), TARGET_FAMILY);
            return;
        }

        try {
            subtreeService.processNetconfSubtree(message);
            LOG.info("Successfully processed NETCONF subtree for job {}", message.getJobId());
        } catch (Exception e) {
            LOG.error("Failed processing NETCONF subtree for job {}: {}", message.getJobId(), e.getMessage(), e);
            // Let the exception propagate so that SQS DLQ handling can take over
            throw e;
        }
    }
}
