package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.LocalDevSetupNetconfService;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class LocalDevSetupNetconfHandler {

    private final LocalDevSetupNetconfService netconfService;

    public LocalDevSetupNetconfHandler(LocalDevSetupNetconfService netconfService) {
        this.netconfService = netconfService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message == null || !"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            // ignore non‑NETCONF messages
            return;
        }
        netconfService.process(message);
    }
}
