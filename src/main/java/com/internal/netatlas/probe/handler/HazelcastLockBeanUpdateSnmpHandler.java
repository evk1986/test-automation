package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.HazelcastLockBeanUpdateSnmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQS handler that processes SNMP walk jobs and guards execution with a Hazelcast distributed lock.
 */
@Service
public class HazelcastLockBeanUpdateSnmpHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HazelcastLockBeanUpdateSnmpHandler.class);

    private final HazelcastLockBeanUpdateSnmpService snmpLockService;

    @Autowired
    public HazelcastLockBeanUpdateSnmpHandler(HazelcastLockBeanUpdateSnmpService snmpLockService) {
        this.snmpLockService = snmpLockService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        LOG.info("Received SNMP walk job for device {} in batch {}", message.getDeviceId(), message.getBatchId());
        snmpLockService.executeSnmpWalk(message);
    }
}
