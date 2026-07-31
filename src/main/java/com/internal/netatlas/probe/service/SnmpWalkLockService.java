package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that guarantees only one SNMP walk runs per device at a time.
 * It uses a distributed Hazelcast lock keyed by the device identifier.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SnmpWalkLockService {

    private final HazelcastInstance hazelcastInstance;

    /**
     * Executes the SNMP walk for the supplied job while holding a distributed lock.
     *
     * @param jobMessage the incoming probe job containing device information
     */
    public void processProbeJob(ProbeJobMessage jobMessage) {
        String lockKey = "snmp-walk-" + jobMessage.getDeviceId();
        ILock lock = hazelcastInstance.getLock(lockKey);
        log.info("Attempting to acquire Hazelcast lock for device {}", jobMessage.getDeviceId());
        lock.lock(); // blocks until the lock is obtained
        try {
            log.info("Lock acquired for device {} – starting SNMP walk", jobMessage.getDeviceId());
            // Simulated SNMP walk – in real code this would invoke the SNMP adapter
            simulateSnmpWalk(jobMessage);
            log.info("SNMP walk completed for device {}", jobMessage.getDeviceId());
        } finally {
            lock.unlock();
            log.info("Lock released for device {}", jobMessage.getDeviceId());
        }
    }

    private void simulateSnmpWalk(ProbeJobMessage jobMessage) {
        // Minimal placeholder to represent work; could be a call to an adapter class.
        try {
            Thread.sleep(100); // simulate latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("SNMP walk simulation interrupted for device {}", jobMessage.getDeviceId());
        }
    }
}
