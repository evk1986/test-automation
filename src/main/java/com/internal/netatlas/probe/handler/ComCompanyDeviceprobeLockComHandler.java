package com.internal.netatlas.probe.handler;

import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.ComCompanyDeviceprobeLockComService;

/**
 * SQS handler for SNMP walk jobs. Delegates the actual processing to the service
 * which contains the Hazelcast lock and Micrometer counter logic.
 */
@Service
public class ComCompanyDeviceprobeLockComHandler {

    private final ComCompanyDeviceprobeLockComService probeService;

    public ComCompanyDeviceprobeLockComHandler(ComCompanyDeviceprobeLockComService probeService) {
        this.probeService = probeService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Basic validation can be added here if needed
        probeService.process(message);
    }
}
