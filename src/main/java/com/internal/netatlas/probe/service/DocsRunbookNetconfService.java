package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.model.ProbeJobStatus;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocsRunbookNetconfService {

    private static final Logger log = LoggerFactory.getLogger(DocsRunbookNetconfService.class);
    private final ProbeJobRepository repository;

    public DocsRunbookNetconfService(ProbeJobRepository repository) {
        this.repository = repository;
    }

    /**
     * Simulates execution of a NETCONF command for the supplied job message.
     * In a real deployment this would invoke a NetconfAdapter that talks to the device.
     */
    public void process(ProbeJobMessage message) {
        Optional<ProbeJob> optionalJob = repository.findById(message.getJobId());
        if (optionalJob.isEmpty()) {
            log.error("ProbeJob {} not found in Cassandra", message.getJobId());
            return;
        }
        ProbeJob job = optionalJob.get();
        try {
            // Simulated NETCONF execution – replace with NetconfAdapter.execute(job) in production
            log.info("Executing simulated NETCONF for device {} (job {})", job.getDeviceId(), job.getId());
            // Assume success
            job.setStatus(ProbeJobStatus.SUCCESS);
            job.setLastErrorMessage(null);
        } catch (Exception e) {
            log.error("NETCONF execution failed for job {}", job.getId(), e);
            job.setStatus(ProbeJobStatus.FAILED);
            job.setLastErrorMessage(e.getMessage());
        } finally {
            job.setAttemptCount(job.getAttemptCount() + 1);
            repository.save(job);
            log.info("Updated ProbeJob {} status to {} after attempt {}", job.getId(), job.getStatus(), job.getAttemptCount());
        }
    }
}
