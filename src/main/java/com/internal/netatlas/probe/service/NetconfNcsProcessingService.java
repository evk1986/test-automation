package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.protocol.NetconfTimeoutException;
import com.internal.netatlas.probe.model.DeviceInfo;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Business logic for handling NETCONF subtree extraction from Cisco IOS‑XR NCS devices.
 * It guarantees exclusive access per device using a Hazelcast distributed lock and records
 * failures via a Micrometer counter.
 */
@Service
public class NetconfNcsProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfNcsProcessingService.class);
    private static final String LOCK_NAME_PREFIX = "netconf-ncs-lock-";
    private static final String SUBTREE_ID = "ncs-config"; // identifier used by the adapter

    private final HazelcastInstance hazelcastInstance;
    private final NetconfAdapter netconfAdapter;
    private final ProbeJobRepository jobRepository;
    private final Counter failureCounter;

    public NetconfNcsProcessingService(HazelcastInstance hazelcastInstance,
                                       NetconfAdapter netconfAdapter,
                                       ProbeJobRepository jobRepository,
                                       MeterRegistry meterRegistry) {
        this.hazelcastInstance = hazelcastInstance;
        this.netconfAdapter = netconfAdapter;
        this.jobRepository = jobRepository;
        this.failureCounter = Counter.builder("probe.protocol.failures")
                .description("Counts NETCONF protocol failures per region")
                .register(meterRegistry);
    }

    /**
     * Executes the NETCONF subtree fetch, maps the raw payload to {@link DeviceInfo},
     * and persists the job status. If the operation times out, the failure counter is
     * incremented and the job status is marked as FAILED.
     */
    public void process(ProbeJobMessage message) {
        String lockName = LOCK_NAME_PREFIX + message.getDeviceId();
        ILock lock = hazelcastInstance.getLock(lockName);
        boolean locked = false;
        try {
            // Try to acquire the lock for up to 5 seconds; if not acquired we skip processing.
            locked = lock.tryLock();
            if (!locked) {
                LOG.warn("Could not acquire lock for device {} – another probe is in progress", message.getDeviceId());
                return;
            }
            LOG.debug("Lock acquired for device {}", message.getDeviceId());

            // Perform the NETCONF call.
            String rawPayload = netconfAdapter.fetchSubtree(message.getDeviceId(), SUBTREE_ID);
            LOG.debug("Fetched subtree for device {}: {}", message.getDeviceId(), rawPayload);

            // Map raw payload to internal DTO.
            DeviceInfo deviceInfo = DeviceInfo.fromRaw(rawPayload);
            LOG.info("Mapped device {} to DeviceInfo: {}", message.getDeviceId(), deviceInfo);

            // Persist successful job outcome.
            jobRepository.save(message.toProbeJob().withStatus("SUCCESS"));
        } catch (NetconfTimeoutException ex) {
            LOG.error("NETCONF timeout for device {}: {}", message.getDeviceId(), ex.getMessage());
            failureCounter.increment();
            // Record failure in the job store.
            jobRepository.save(message.toProbeJob().withStatus("FAILED").withLastErrorMessage(ex.getMessage()));
        } catch (Exception ex) {
            LOG.error("Unexpected error processing NETCONF NCS job {}: {}", message.getJobId(), ex.getMessage(), ex);
            failureCounter.increment();
            jobRepository.save(message.toProbeJob().withStatus("FAILED").withLastErrorMessage(ex.getMessage()));
        } finally {
            if (locked) {
                lock.unlock();
                LOG.debug("Lock released for device {}", message.getDeviceId());
            }
        }
    }
}
