package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetconfBatchRetryService {

    private final ProbeJobRepository probeJobRepository;
    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public NetconfBatchRetryService(ProbeJobRepository probeJobRepository, HazelcastInstance hazelcastInstance) {
        this.probeJobRepository = probeJobRepository;
        this.hazelcastInstance = hazelcastInstance;
    }

    public void retryFailedJobs(String batchId) {
        List<ProbeJob> failedJobs = probeJobRepository.findByBatchIdAndStatus(batchId, "FAILED");
        ILock lock = hazelcastInstance.getLock("netconf-batch-retry-lock");
        lock.lock();
        try {
            for (ProbeJob job : failedJobs) {
                // retry logic
                job.setStatus("RUNNING");
                probeJobRepository.save(job);
            }
        } finally {
            lock.unlock();
        }
    }
}