package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.HazelcastLockImplementationMicrometerCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQS handler for SNMP walk jobs. Acquires a distributed lock per device/batch before
 * invoking the walk logic and records protocol‑specific failure metrics.
 */
@Service
public class SnmpWalkJobHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpWalkJobHandler.class);

    private final HazelcastLockImplementationMicrometerCounter lockService;

    @Autowired
    public SnmpWalkJobHandler(HazelcastLockImplementationMicrometerCounter lockService) {
        this.lockService = lockService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        String deviceId = message.getDeviceId();
        String batchId = message.getBatchId();
        String protocol = message.getProtocol();
        String region = message.getRegion();

        LOG.info("Received SNMP walk job for device {} batch {}", deviceId, batchId);

        boolean lockAcquired = lockService.acquireLock(deviceId, batchId);
        if (!lockAcquired) {
            LOG.warn("Lock not acquired for device {} batch {} – another worker is processing", deviceId, batchId);
            // Re‑queue or DLQ handling would be done by the orchestrator; we simply return.
            return;
        }
        try {
            // Simulated SNMP walk – in real code this would delegate to an SNMP adapter.
            boolean success = lockService.performSnmpWalkSimulation(message);
            if (!success) {
                lockService.recordFailure(protocol, region);
                LOG.error("SNMP walk failed for device {} (protocol={}, region={})", deviceId, protocol, region);
            } else {
                LOG.info("SNMP walk succeeded for device {}", deviceId);
            }
        } finally {
            lockService.releaseLock(deviceId, batchId);
        }
    }
}
