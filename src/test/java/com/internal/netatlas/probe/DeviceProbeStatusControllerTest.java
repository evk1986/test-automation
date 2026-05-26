package com.internal.netatlas.probe;

import com.internal.netatlas.probe.controller.DeviceProbeStatusController;
import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.service.DeviceProbeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeviceProbeStatusControllerTest {
    @Mock
    private DeviceProbeService deviceProbeService;

    @InjectMocks
    private DeviceProbeStatusController deviceProbeStatusController;

    @Test
    public void testGetProbeJobStatus() {
        ProbeJob probeJob = new ProbeJob();
        probeJob.setStatus("RUNNING");
        when(deviceProbeService.getProbeJob("jobId")).thenReturn(probeJob);
        ResponseEntity<String> response = deviceProbeStatusController.getProbeJobStatus("jobId");
        assertEquals("RUNNING", response.getBody());
    }
}