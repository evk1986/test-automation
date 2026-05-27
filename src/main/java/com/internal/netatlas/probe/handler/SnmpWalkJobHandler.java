package com.internal.netatlas.probe.handler;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.SnmppWalkJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SnmpWalkJobHandler {

    private final SnmppWalkJobService snmppWalkJobService;
    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public SnmpWalkJobHandler(SnmppWalkJobService snmppWalkJobService, HazelcastInstance hazelcastInstance) {
        this.snmppWalkJobService = snmppWalkJobService;
        this.hazelcastInstance = hazelcastInstance;
    }

    @SqsListener(value = "probe.commands", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void handle(@Payload ProbeJobMessage message, @Headers Map<String, String> headers) {
        String deviceId = message.getDeviceId();
        String batchId = message.getBatchId();
        String lockName = "snmp-walk-lock-" + deviceId + ":" + batchId;
        hazelcastInstance.getLock(lockName).lock();
        try {
            snmppWalkJobService.processSnmpWalkJob(message);
        } finally {
            hazelcastInstance.getLock(lockName).unlock();
        }
    }
}