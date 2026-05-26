package com.internal.netatlas.probe.controller;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.service.DeviceProbeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/probe/jobs")
public class DeviceProbeStatusController {
    private final DeviceProbeService deviceProbeService;

    @Autowired
    public DeviceProbeStatusController(DeviceProbeService deviceProbeService) {
        this.deviceProbeService = deviceProbeService;
    }

    @GetMapping("/{jobId}/status")
    public ResponseEntity<String> getProbeJobStatus(@PathVariable String jobId) {
        ProbeJob probeJob = deviceProbeService.getProbeJob(jobId);
        return ResponseEntity.ok(probeJob.getStatus());
    }
}