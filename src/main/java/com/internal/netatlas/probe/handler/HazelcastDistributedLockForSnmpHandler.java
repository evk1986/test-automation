package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.service.HazelcastDistributedLockForSnmpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class HazelcastDistributedLockForSnmpHandler {

    private static final Logger log = LoggerFactory.getLogger(HazelcastDistributedLockForSnmpHandler.class);
    private final HazelcastDistributedLockForSnmpService lockService;

    public HazelcastDistributedLockForSnmpHandler(HazelcastDistributedLockForSnmpService lockService) {
        this.lockService = lockService;
    }

    @SqsListener("probe.commands")
    public void handle(SnmpProbeCommand command) {
        if (command == null || command.deviceId() == null) {
            log.warn("Received invalid or null SNMP probe command");
            return;
        }

        log.info("Processing probe command for deviceId={} messageId={}", command.deviceId(), command.messageId());
        boolean success = lockService.processSnmpWalkWithLock(command.deviceId(), command.messageId());
        if (!success) {
            log.info("Skipped execution for deviceId={} messageId={}", command.deviceId(), command.messageId());
        }
    }

    public record SnmpProbeCommand(String deviceId, String messageId, String batchId, String protocol) {}
}
