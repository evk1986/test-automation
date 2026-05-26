package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NetconfRetryService {
    private static final Logger LOGGER = Logger.getLogger(NetconfRetryService.class.getName());

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public NetconfRetryService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void retryFailedJob(ProbeJob probeJob) {
        // Implement NETCONF retry logic here
        probeJob.setStatus("RUNNING");
        probeJobRepository.save(probeJob);
    }
}