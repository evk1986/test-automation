package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.DeviceSnapshot;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.DeviceSnapshotRepository;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

/**
 * Business logic for handling NETCONF subtree extraction on Cisco IOS‑XR NCS devices.
 * The service updates the {@link ProbeJob} status, persists a {@link DeviceSnapshot}
 * with the raw NETCONF RPC payload, and prepares the message for the next pipeline stage.
 */
@Service
public class ProbeHandlersNetconfSubtreeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProbeHandlersNetconfSubtreeService.class);

    private final ProbeJobRepository jobRepository;
    private final DeviceSnapshotRepository snapshotRepository;

    public ProbeHandlersNetconfSubtreeService(ProbeJobRepository jobRepository,
                                               DeviceSnapshotRepository snapshotRepository) {
        this.jobRepository = jobRepository;
        this.snapshotRepository = snapshotRepository;
    }

    /**
     * Executes the NETCONF subtree request, stores the raw payload and updates job state.
     *
     * @param message incoming probe job message
     */
    @Transactional
    public void processNetconfSubtree(ProbeJobMessage message) {
        // 1. Load the job record (if missing we treat it as a fatal error)
        ProbeJob job = jobRepository.findById(message.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("ProbeJob not found: " + message.getJobId()));

        // 2. Mark job as RUNNING
        job.setStatus(ProbeJob.Status.RUNNING);
        jobRepository.save(job);

        // 3. Build a minimal NETCONF <get> RPC for the requested subtree.
        //    In a real implementation this would be built from a device‑specific template.
        String rpcPayload = "<rpc message-id=\"1\" xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\">" +
                "<get><filter type=\"subtree\">" +
                "<configuration xmlns=\"http://cisco.com/ns/yang/Cisco-IOS-XR-ifmgr-cfg\"/>" +
                "</filter></get></rpc>";

        // 4. Persist the raw snapshot
        DeviceSnapshot snapshot = new DeviceSnapshot();
        snapshot.setId(UUID.randomUUID().toString());
        snapshot.setDeviceId(message.getDeviceId());
        snapshot.setProtocol(message.getProtocol());
        snapshot.setRawPayload(rpcPayload);
        snapshot.setCollectedAt(Instant.now());
        snapshot.setJobId(message.getJobId());
        snapshotRepository.save(snapshot);
        LOG.debug("Persisted DeviceSnapshot {} for job {}", snapshot.getId(), message.getJobId());

        // 5. Update job status to SUCCESS
        job.setStatus(ProbeJob.Status.SUCCESS);
        jobRepository.save(job);
    }
}
