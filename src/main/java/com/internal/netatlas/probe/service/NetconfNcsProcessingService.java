package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Service
public class NetconfNcsProcessingService {
    private static final Logger LOG = LoggerFactory.getLogger(NetconfNcsProcessingService.class);

    /**
     * Executes the NETCONF subtree retrieval for a Cisco IOS‑XR NCS device.
     * The method includes a simple retry policy to handle transient connectivity issues.
     */
    @Retryable(value = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void process(ProbeJobMessage message) {
        try {
            // In a real implementation this would open a NETCONF session, send <get> requests,
            // and publish the raw payload to the "normalize.ingest" queue.
            LOG.info("Invoking NETCONF subtree handler for job {} (device {})", message.getJobId(), message.getDeviceId());
            // Simulate successful processing
            // e.g., netconfAdapter.executeSubtree(message.getDeviceId(), "<filter type='subtree'>...</filter>");
        } catch (Exception ex) {
            LOG.error("NETCONF processing failed for job {}: {}", message.getJobId(), ex.getMessage());
            // Propagate to trigger retry logic
            throw new RuntimeException(ex);
        }
    }
}
