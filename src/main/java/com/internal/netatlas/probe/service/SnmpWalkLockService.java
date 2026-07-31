package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.repository.DeviceSnapshotRepository;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class SnmpWalkLockService {

    private static final Logger LOG = LoggerFactory.getLogger(SnmpWalkLockService.class);
    private static final String LOCK_MAP_NAME = "snmp-walk-locks";
    private static final long LOCK_TIMEOUT_SECONDS = 5L;

    private final HazelcastInstance hazelcastInstance;
    private final DeviceSnapshotRepository snapshotRepository;

    public SnmpWalkLockService(HazelcastInstance hazelcastInstance,
                               DeviceSnapshotRepository snapshotRepository) {
        this.hazelcastInstance = hazelcastInstance;
        this.snapshotRepository = snapshotRepository;
    }

    /**
     * Executes an SNMP walk for the device described in the message.
     * A Hazelcast distributed lock guarantees that only one worker processes
     * a given device at a time. The lock entry is removed (released) after the
     * walk completes, ensuring subsequent polls can proceed.
     *
     * @param message the incoming probe job request
     * @throws IllegalStateException if the lock cannot be obtained within the timeout
     */
    public void processSnmpWalk(ProbeJobMessage message) {
        IMap<String, String> lockMap = hazelcastInstance.getMap(LOCK_MAP_NAME);
        String deviceKey = message.getDeviceId();
        boolean lockAcquired = false;
        try {
            lockAcquired = lockMap.tryLock(deviceKey, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!lockAcquired) {
                LOG.warn("Unable to acquire SNMP walk lock for device {} within {} seconds", deviceKey, LOCK_TIMEOUT_SECONDS);
                throw new IllegalStateException("SNMP walk lock not acquired for device " + deviceKey);
            }
            LOG.info("Lock acquired for device {} – starting SNMP walk", deviceKey);
            // -----------------------------------------------------------------
            // Simulated SNMP walk – in real code this would invoke the SNMP client.
            // -----------------------------------------------------------------
            String rawPayload = "<snmp>simulated-response</snmp>"; // placeholder payload
            DeviceSnapshot snapshot = new DeviceSnapshot();
            snapshot.setId(message.getJobId() + "-" + deviceKey);
            snapshot.setDeviceId(deviceKey);
            snapshot.setProtocol(message.getProtocol());
            snapshot.setRawPayload(rawPayload);
            snapshot.setCollectedAt(Instant.now());
            snapshot.setJobId(message.getJobId());
            snapshotRepository.save(snapshot);
            LOG.info("SNMP walk completed and snapshot persisted for device {}", deviceKey);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for SNMP lock", e);
        } finally {
            if (lockAcquired) {
                lockMap.unlock(deviceKey);
                LOG.info("Lock released for device {}", deviceKey);
            }
        }
    }
}
