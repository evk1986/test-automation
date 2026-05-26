package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.service.NetconfRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NetconfWorkerHandler {

    private final NetconfAdapter netconfAdapter;
    private final NetconfRetryService netconfRetryService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public NetconfWorkerHandler(NetconfAdapter netconfAdapter, NetconfRetryService netconfRetryService, QueueMessagingTemplate queueMessagingTemplate) {
        this.netconfAdapter = netconfAdapter;
        this.netconfRetryService = netconfRetryService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        ProbeJob probeJob = message.getProbeJob();
        if (probeJob.getProtocol().equals("NETCONF")) {
            try {
                netconfAdapter.connectAndExecute(probeJob);
            } catch (Exception e) {
                netconfRetryService.retry(probeJob, e);
            }
        }
    }
}