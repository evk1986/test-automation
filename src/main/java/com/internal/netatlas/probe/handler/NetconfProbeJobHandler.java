package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfIntegrationJobService;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NetconfProbeJobHandler {

    private static final Logger log = LoggerFactory.getLogger(NetconfProbeJobHandler.class);
    private final NetconfIntegrationJobService netconfIntegrationJobService;

    public NetconfProbeJobHandler(NetconfIntegrationJobService netconfIntegrationJobService) {
        this.netconfIntegrationJobService = netconfIntegrationJobService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message == null || message.getJobId() == null) {
            log.warn("Received empty or malformed ProbeJobMessage on probe.commands queue");
            return;
        }

        log.info("Received NETCONF probe job command: jobId={}, deviceId={}, batchId={}",
                message.getJobId(), message.getDeviceId(), message.getBatchId());

        netconfIntegrationJobService.processNetconfCommand(message);
    }
}
