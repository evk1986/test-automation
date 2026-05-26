package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceProbeService {
    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public DeviceProbeService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public ProbeJob getProbeJob(String jobId) {
        return probeJobRepository.findById(jobId).orElseThrow();
    }
}