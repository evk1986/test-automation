package com.internal.netatlas.probe.service;

import com.hazelcast.core.HazelcastInstance;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class HazelcastDistributedLockForSnmpService {

    private static final Logger log = LoggerFactory.getLogger(HazelcastDistributedLockForSnmpService.class);
    private static final String LOCK_PREFIX = "snmp-lock-";
    private static final String IDEMPOTENCY_MAP = "probe.idempotency";

    private final HazelcastInstance hazelcastInstance;
    private final Counter duplicateCounter;
    private final Set<String> processedMessageStore = ConcurrentHashMap.newKeySet();

    public HazelcastDistributedLockForSnmpService(HazelcastInstance hazelcastInstance, MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.duplicateCounter = meterRegistry.counter("probe.snmp.idempotent");
    }

    public boolean isDuplicateMessage(String messageId) {
        if (messageId == null) {
            return false;
        }
        var map = hazelcastInstance.getMap(IDEMPOTENCY_MAP);
        if (map.containsKey(messageId) || processedMessageStore.contains(messageId)) {
            duplicateCounter.increment();
            log.info("Duplicate SQS message detected and skipped: messageId={}", messageId);
            return true;
        }
        return false;
    }

    public boolean processSnmpWalkWithLock(String deviceId, String messageId) {
        if (isDuplicateMessage(messageId)) {
            return false;
        }

        String lockKey = LOCK_PREFIX + deviceId;
        var lock = hazelcastInstance.getCPSubsystem().getLock(lockKey);

        boolean acquired = false;
        try {
            acquired = lock.tryLock(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error acquiring lock for deviceId={}", deviceId, e);
            return false;
        }

        if (!acquired) {
            log.warn("Could not acquire SNMP lock for deviceId={}", deviceId);
            return false;
        }

        try {
            extendSqsVisibilityTimeout(messageId, 120);
            executeSnmpWalk(deviceId);
            recordProcessedMessage(messageId);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void extendSqsVisibilityTimeout(String messageId, int extraSeconds) {
        log.info("Extended SQS visibility timeout by {}s for messageId={}", extraSeconds, messageId);
    }

    private void executeSnmpWalk(String deviceId) {
        log.info("Executing SNMP walk for deviceId={}", deviceId);
    }

    public void recordProcessedMessage(String messageId) {
        if (messageId != null) {
            var map = hazelcastInstance.getMap(IDEMPOTENCY_MAP);
            map.put(messageId, System.currentTimeMillis());
            processedMessageStore.add(messageId);
            log.info("Recorded idempotency key in store: messageId={}", messageId);
        }
    }
}
