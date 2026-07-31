package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.repository.DeviceSnapshotRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProbeHandlersNetconfSubtreeService {

    private final NetconfAdapter netconfAdapter;
    private final DeviceSnapshotRepository snapshotRepository;
    private final Counter netconfFailureCounter;

    public ProbeHandlersNetconfSubtreeService(NetconfAdapter netconfAdapter,
                                              DeviceSnapshotRepository snapshotRepository,
                                              MeterRegistry meterRegistry) {
        this.netconfAdapter = netconfAdapter;
        this.snapshotRepository = snapshotRepository;
        this.netconfFailureCounter = Counter.builder("probe.protocol.failures")
                .description("Count of protocol failures per protocol and region")
                .tags("protocol", "NETCONF", "region", "us-east-1")
                .register(meterRegistry);
    }

    /**
     * Executes a NETCONF subtree query for the given job. On success the raw payload is persisted as a
     * {@link DeviceSnapshot}. On any exception the failure counter is incremented so that the metric
     * {@code probe.protocol.failures} with tags {@code protocol=NETCONF, region=us-east-1} reflects the error.
     */
    public void processSubtree(ProbeJobMessage message) {
        try {
            String rawPayload = netconfAdapter.executeSubtree(message);
            DeviceSnapshot snapshot = new DeviceSnapshot();
            snapshot.setId(UUID.randomUUID().toString());
            snapshot.setDeviceId(message.getDeviceId());
            snapshot.setProtocol(message.getProtocol());
            snapshot.setRawPayload(rawPayload);
            snapshot.setCollectedAt(Instant.now());
            snapshot.setJobId(message.getId());
            snapshotRepository.save(snapshot);
        } catch (Exception e) {
            // Increment the Micrometer counter for NETCONF failures in us-east-1 region
            netconfFailureCounter.increment();
            // In a real implementation we would also log the error and possibly move the message to a DLQ
        }
    }
}
