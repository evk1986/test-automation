package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfBatchProcessingService;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class NetconfBatchHandler {

    private final NetconfBatchProcessingService processingService;

    public NetconfBatchHandler(NetconfBatchProcessingService processingService) {
        this.processingService = processingService;
    }

    /**
     * Consumes messages from the {@code probe.commands} queue. Only NETCONF protocol messages are processed.
     */
    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message == null) {
            return;
        }
        // Guard against non‑NETCONF jobs – other handlers will pick those up.
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            return;
        }
        String batchId = message.getBatchId();
        if (batchId != null && !batchId.isBlank()) {
            processingService.processBatch(batchId);
        }
    }
}
