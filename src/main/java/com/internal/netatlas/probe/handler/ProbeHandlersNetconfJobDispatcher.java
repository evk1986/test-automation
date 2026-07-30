package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfSubtreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

/**
 * SQS handler that receives NETCONF probe jobs and delegates to the subtree service.
 */
@Service
public class ProbeHandlersNetconfJobDispatcher {

    private final NetconfSubtreeService subtreeService;

    @Autowired
    public ProbeHandlersNetconfJobDispatcher(NetconfSubtreeService subtreeService) {
        this.subtreeService = subtreeService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Only process NETCONF protocol messages; other protocols are ignored by this handler.
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            return;
        }
        subtreeService.dispatchSubtree(message);
    }
}
