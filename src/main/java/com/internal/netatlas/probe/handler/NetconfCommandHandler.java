package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class NetconfCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(NetconfCommandHandler.class);
    private final NetconfSessionService sessionService;

    public NetconfCommandHandler(NetconfSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @JmsListener(destination = "probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message == null || message.getJobId() == null) {
            log.warn("Received empty or invalid probe job message from probe.commands");
            return;
        }
        log.info("Processing NETCONF command for job ID: {}, device ID: {}", message.getJobId(), message.getDeviceId());
        sessionService.executeSubtreeQuery(message.getJobId(), message.getDeviceId(), message.getBatchId());
    }
}
