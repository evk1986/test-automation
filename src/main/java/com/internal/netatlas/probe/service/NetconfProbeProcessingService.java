package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Minimal processing service for NETCONF probe jobs. In a real implementation this would
 * establish a NETCONF session, execute the required RPCs and persist the raw payload.
 */
@Service
public class NetconfProbeProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(NetconfProbeProcessingService.class);

    public void process(ProbeJobMessage message) {
        // Placeholder for the actual NETCONF interaction logic.
        logger.info("Processing NETCONF probe for jobId={}, deviceId={}, protocol={}",
                message.getJobId(), message.getDeviceId(), message.getProtocol());
        // Simulate processing delay (optional)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
