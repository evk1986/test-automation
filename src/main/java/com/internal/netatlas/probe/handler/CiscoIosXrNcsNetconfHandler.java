package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.service.NetconfBatchRetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class CiscoIosXrNcsNetconfHandler {

    private final NetconfAdapter netconfAdapter;
    private final NetconfBatchRetryService retryService;

    @Autowired
    public CiscoIosXrNcsNetconfHandler(NetconfAdapter netconfAdapter, NetconfBatchRetryService retryService) {
        this.netconfAdapter = netconfAdapter;
        this.retryService = retryService;
    }

    @SqsListener(value = "probe.commands", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void handleNetconfJob(ProbeJobMessage message) {
        // Connect to Cisco IOS-XR NCS device using NETCONF
        String deviceId = message.getDeviceId();
        String protocol = message.getProtocol();
        if (protocol.equals("NETCONF")) {
            // Collect and process NETCONF responses
            String response = netconfAdapter.collectResponse(deviceId);
            // Process the response and update the job status
            retryService.updateJobStatus(deviceId, response);
        }
    }
}