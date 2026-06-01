package com.internal.netatlas.probe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceProbeDockerComposeConfig {
    @Value("${docker.compose.file}")
    private String dockerComposeFile;

    public String getDockerComposeFile() {
        return dockerComposeFile;
    }
}