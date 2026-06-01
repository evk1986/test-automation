package com.internal.netatlas.probe;

import com.internal.netatlas.probe.service.DeviceProbeOnboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeviceProbeOnboardTest {
    @Autowired
    private DeviceProbeOnboardService deviceProbeOnboardService;

    @Test
    public void testOnboardDeviceProbeLocalDockerComposeEnvironment() {
        deviceProbeOnboardService.onboardDeviceProbeLocalDockerComposeEnvironment();
    }
}