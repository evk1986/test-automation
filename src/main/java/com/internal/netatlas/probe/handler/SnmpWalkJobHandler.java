package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.SnmpWalkLockService;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;

/**
 * SQS listener that receives SNMP walk jobs and delegates processing to the lock service.
 */
@Service
@RequiredArgsConstructor
public class SnmpWalkJobHandler {

    private final SnmpWalkLockService snmpWalkLockService;

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Basic validation – ensure required fields are present
        if (message == null || message.getDeviceId() == null) {
            throw new IllegalArgumentException("ProbeJobMessage or deviceId is missing");
        }
        // Delegate to the service that serialises SNMP walks per device using Hazelcast lock
        snmpWalkLockService.processProbeJob(message);
    }
}
