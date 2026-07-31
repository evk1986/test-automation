package com.internal.netatlas.probe.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.HazelcastLockWrapperIdempotencyKeyService;

@Service
public class SnmpWalkJobHandler {

    private static final Logger logger = LoggerFactory.getLogger(SnmpWalkJobHandler.class);
    private final HazelcastLockWrapperIdempotencyKeyService processingService;

    @Autowired
    public SnmpWalkJobHandler(HazelcastLockWrapperIdempotencyKeyService processingService) {
        this.processingService = processingService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        logger.info("Received SNMP walk job for device {} (messageId={})", message.getDeviceId(), message.getMessageId());
        processingService.processSnmpWalk(message);
    }
}
