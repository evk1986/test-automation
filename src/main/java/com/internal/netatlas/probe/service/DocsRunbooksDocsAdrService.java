package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.internal.netatlas.probe.publisher.SnsPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class DocsRunbooksDocsAdrService {

    private final ProbeJobRepository jobRepository;
    private final SnsPublisher snsPublisher;

    public DocsRunbooksDocsAdrService(ProbeJobRepository jobRepository, SnsPublisher snsPublisher) {
        this.jobRepository = jobRepository;
        this.snsPublisher = snsPublisher;
    }

    /**
     * Executes the minimal NETCONF job flow: mark the job as RUNNING, simulate a raw payload,
     * and publish a {@link DeviceSnapshot} to the downstream SNS topic.
     */
    public void processNetconfJob(ProbeJobMessage message) {
        // Retrieve the job record – fail fast if it does not exist
        ProbeJob job = jobRepository.findById(message.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("ProbeJob not found: " + message.getJobId()));

        // Update status and attempt count
        job.setStatus(ProbeJob.Status.RUNNING);
        job.setAttemptCount(job.getAttemptCount() + 1);
        jobRepository.save(job);

        // Build a synthetic DeviceSnapshot representing the NETCONF reply
        DeviceSnapshot snapshot = new DeviceSnapshot();
        snapshot.setId(UUID.randomUUID().toString());
        snapshot.setDeviceId(message.getDeviceId());
        snapshot.setProtocol(message.getProtocol());
        snapshot.setRawPayload("<rpc-reply>Mock NETCONF data for device " + message.getDeviceId() + "</rpc-reply>");
        snapshot.setCollectedAt(Instant.now());
        snapshot.setJobId(message.getJobId());

        // Publish the snapshot – downstream services (Schema‑Normalizer, Data‑Enricher) consume it
        snsPublisher.publish("platform.results", snapshot);
    }
}
