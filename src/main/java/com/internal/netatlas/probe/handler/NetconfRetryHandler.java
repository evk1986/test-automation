package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NetconfRetryHandler {
    private static final Logger LOGGER = Logger.getLogger(NetconfRetryHandler.class.getName());

    private final NetconfRetryService netconfRetryService;

    @Autowired
    public NetconfRetryHandler(NetconfRetryService netconfRetryService) {
        this.netconfRetryService = netconfRetryService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        ProbeJob probeJob = message.getProbeJob();
        if (probeJob.getProtocol().equals("NETCONF")) {
            netconfRetryService.retryFailedJob(probeJob);
        }
    }
}