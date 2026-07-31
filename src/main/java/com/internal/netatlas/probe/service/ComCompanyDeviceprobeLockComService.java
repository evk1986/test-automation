package com.internal.netatlas.probe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.protocol.SnmpAdapter;

/**
 * Service that performs an SNMP walk while ensuring that only a single walk per
 * device-id runs concurrently within the same batch. It also records protocol‑
 * specific failures using a Micrometer counter.
 */
@Service
public class ComCompanyDeviceprobeLockComService {

    private static final Logger logger = LoggerFactory.getLogger(ComCompanyDeviceprobeLockComService.class);
    private static final String LOCK_MAP_NAME = "PROBE_LOCKS";

    private final HazelcastInstance hazelcastInstance;
    private final SnmpAdapter snmpAdapter;
    private final MeterRegistry meterRegistry;

    public ComCompanyDeviceprobeLockComService(HazelcastInstance hazelcastInstance,
                                               SnmpAdapter snmpAdapter,
                                               MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.snmpAdapter = snmpAdapter;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Executes the SNMP walk for the given job. The method acquires a distributed
     * lock keyed by device-id to serialize concurrent executions. Any exception
     * increments the {@code probe.protocol.failures} counter with tags for the
     * protocol and region.
     */
    public void process(ProbeJobMessage message) {
        String lockKey = LOCK_MAP_NAME + ":" + message.getDeviceId();
        ILock lock = hazelcastInstance.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock();
            if (!acquired) {
                logger.info("SNMP walk already in progress for device {} in batch {}",
                        message.getDeviceId(), message.getBatchId());
                return;
            }
            // Perform the actual SNMP walk – implementation details are inside the adapter
            snmpAdapter.walk(message.getDeviceId(), message.getRegion());
        } catch (Exception ex) {
            Counter counter = Counter.builder("probe.protocol.failures")
                    .tag("protocol", "SNMP")
                    .tag("region", message.getRegion())
                    .register(meterRegistry);
            counter.increment();
            logger.error("SNMP walk failed for device {}: {}", message.getDeviceId(), ex.getMessage());
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}
