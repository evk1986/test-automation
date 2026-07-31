package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.ProbeHandlersNetconfSubtreeService;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class ProbeHandlersNetconfSubtreeHandler {

    private final ProbeHandlersNetconfSubtreeService subtreeService;

    public ProbeHandlersNetconfSubtreeHandler(ProbeHandlersNetconfSubtreeService subtreeService) {
        this.subtreeService = subtreeService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Only process NETCONF jobs; other protocols are ignored by this handler
        if ("NETCONF".equalsIgnoreCase(message.getProtocol())) {
            subtreeService.processSubtree(message);
        }
    }
}
