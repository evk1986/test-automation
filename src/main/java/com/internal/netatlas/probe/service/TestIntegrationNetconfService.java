package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.internal.netatlas.probe.publisher.SnsPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TestIntegrationNetconfService {

    private final ProbeJobRepository repository;
    private final SnsPublisher snsPublisher;

    public TestIntegrationNetconfService(ProbeJobRepository repository, SnsPublisher snsPublisher) {
        this.repository = repository;
        this.snsPublisher = snsPublisher;
    }

    /**
     * Persists a raw NETCONF payload snapshot and notifies the normalizer.
     * The payload is a placeholder because the integration test focuses on the
     * end‑to‑end flow rather than the actual device interaction.
     */
    public void processProbeJob(ProbeJobMessage message) {
        DeviceSnapshot snapshot = new DeviceSnapshot();
        snapshot.setId(UUID.randomUUID().toString());
        snapshot.setDeviceId(message.getDeviceId());
        snapshot.setProtocol(message.getProtocol());
        snapshot.setRawPayload("<rpc><get><filter type='subtree'>...</filter></get></rpc>");
        snapshot.setCollectedAt(Instant.now());
        snapshot.setJobId(message.getId());

        repository.save(snapshot);

        // Publish the snapshot identifier so the Schema‑Normalizer can ingest it
        snsPublisher.publish("normalize.ingest", snapshot.getId());
    }
}
