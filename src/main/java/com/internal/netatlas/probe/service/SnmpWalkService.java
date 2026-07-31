package com.internal.netatlas.probe.service;

import com.hazelcast.core.ILock;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SnmpWalkService {

    private static final Logger logger = LoggerFactory.getLogger(SnmpWalkService.class);
    private final ProbeJobRepository jobRepository;
    private final ILock snmpWalkLock;

    public SnmpWalkService(ProbeJobRepository jobRepository, ILock snmpWalkLock) {
        this.jobRepository = jobRepository;
        this.snmpWalkLock = snmpWalkLock;
    }

    public void performWalk(ProbeJob job) {
        // Using a global lock; can be refined per device if needed
        try {
            boolean acquired = snmpWalkLock.tryLock();
            if (!acquired) {
                logger.warn("SNMP walk lock not acquired for device {}", job.getDeviceId());
                job.setStatus("DLQ");
                jobRepository.save(job);
                return;
            }
            logger.info("Acquired SNMP walk lock for device {}", job.getDeviceId());
            // Simulated SNMP walk work (replace with real adapter call)
            Thread.sleep(100);
            job.setStatus("SUCCESS");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            job.setStatus("FAILED");
            job.setLastErrorMessage(e.getMessage());
        } finally {
            if (snmpWalkLock.isHeldByCurrentThread()) {
                snmpWalkLock.unlock();
                logger.info("Released SNMP walk lock for device {}", job.getDeviceId());
            }
            jobRepository.save(job);
        }
    }
}
