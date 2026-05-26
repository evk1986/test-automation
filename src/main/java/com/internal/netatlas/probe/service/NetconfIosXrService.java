package com/internal/netatlas/probe/service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import org.springframework.stereotype.Service;

@Service
public class NetconfIosXrService {
    public void processProbeJob(ProbeJobMessage message) {
        // Process probe job using NETCONF protocol for Cisco IOS-XR devices
        // This is a placeholder for the actual implementation
        System.out.println("Processing probe job for Cisco IOS-XR device");
    }
}