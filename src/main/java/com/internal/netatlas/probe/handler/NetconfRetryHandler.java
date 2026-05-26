package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.stereotype.Service;

@Service
public class NetconfRetryHandler {
    private final NetconfBatchRetryService netconfBatchRetryService;
    private final QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    public NetconfRetryHandler(NetconfBatchRetryService netconfBatchRetryService, QueueMessagingTemplate queueMessagingTemplate) {
        this.netconfBatchRetryService = netconfBatchRetryService;
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void handle(ProbeJobMessage message) {
        ProbeJob probeJob = message.getProbeJob();
        if (probeJob.getProtocol().equals("NETCONF")) {
            netconfBatchRetryService.retryFailedJobs(probeJob.getBatchId());
        }
    }
}