package com.internal.netatlas.probe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceProbeLocalEnvironmentController {
    @GetMapping("/device-probe/local-environment")
    public String getLocalEnvironment() {
        return "Device-Probe local environment setup complete";
    }
}