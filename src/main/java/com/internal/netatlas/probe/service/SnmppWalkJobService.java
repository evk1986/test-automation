package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.repository.ProbeJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SnmppWalkJobService {

    private final ProbeJobRepository probeJobRepository;

    @Autowired
    public SnmppWalkJobService(ProbeJobRepository probeJobRepository) {
        this.probeJobRepository = probeJobRepository;
    }

    public void processSnmpWalkJob(ProbeJobMessage message) {
        // Process SNMP walk job logic here
        // For example:
        // probeJobRepository.save(message);
    }
}