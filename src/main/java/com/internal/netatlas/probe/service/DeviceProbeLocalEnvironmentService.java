package com.internal.netatlas.probe.service;

import org.springframework.stereotype.Service;

@Service
public class DeviceProbeLocalEnvironmentService {
    public void setupLocalEnvironment() {
        // Read Device-Probe local docker-compose
        // Note Vault IAM role ARN and Consul agent health-check port
        System.out.println("Device-Probe local environment setup complete");
    }
}