package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.service.ProbeLockProbeMetricsService;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service;
public class ProbeLockProbeMetricsHandler {

    private static final Logger log = LoggerFactory.getLogger(ProbeLockProbeMetricsHandler.class);
    private final ProbeLockProbeMetricsService service;

    public ProbeLockProbeMetricsHandler(ProbeLockProbeMetricsService service) {
        this.service = service;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJob job) {
        log.info("Received probe job message for deviceId={}, protocol={}", job.getDeviceId(), job.getProtocol());
        boolean executed = service.executeWithLockAndMetrics(job, () -> {
            log.info("Executing device probe task for deviceId={} using protocol={}", job.getDeviceId(), job.getProtocol());
        });
        if (!executed) {
            log.warn("Probe job execution failed or was locked out for jobId={}", job.getId());
        }
    }
}