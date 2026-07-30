package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.protocol.NetconfAdapter;
import com.internal.netatlas.probe.repository.DeviceSnapshotRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Business logic for executing a NETCONF subtree query against a Cisco IOS‑XR NCS device.
 * On success the raw payload is persisted as a {@link DeviceSnapshot}. On failure a
 * Micrometer counter (probe.protocol.failures) is incremented with tags protocol=netconf
 * and region=us-east-1.
 */
@Service
public class NetconfSubtreeService {

    private final NetconfAdapter netconfAdapter;
    private final DeviceSnapshotRepository snapshotRepository;
    private final Counter failureCounter;

    public NetconfSubtreeService(NetconfAdapter netconfAdapter,
                                 DeviceSnapshotRepository snapshotRepository,
                                 MeterRegistry meterRegistry) {
        this.netconfAdapter = netconfAdapter;
        this.snapshotRepository = snapshotRepository;
        this.failureCounter = Counter.builder("probe.protocol.failures")
                .tag("protocol", "netconf")
                .tag("region", "us-east-1")
                .register(meterRegistry);
    }

    public void dispatchSubtree(ProbeJobMessage message) {
        try {
            // The subtree identifier is specific to Cisco IOS‑XR NCS devices.
            String subtree = "Cisco-IOS-XR-infra-cfg:subtree";
            String rawResponse = netconfAdapter.executeSubtree(message.getDeviceId(),
                    message.getJobId(),
                    subtree);

            DeviceSnapshot snapshot = new DeviceSnapshot();
            snapshot.setId(message.getJobId());
            snapshot.setDeviceId(message.getDeviceId());
            snapshot.setProtocol(message.getProtocol());
            snapshot.setRawPayload(rawResponse);
            snapshot.setCollectedAt(Instant.now());
            snapshot.setJobId(message.getJobId());

            snapshotRepository.save(snapshot);
        } catch (Exception e) {
            // Increment the failure metric; the exception is swallowed to let the pipeline
            // continue processing other messages. Detailed logging would be added in a real
            // implementation.
            failureCounter.increment();
        }
    }
}
