package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJob;
import com.internal.netatlas.probe.service.ProbeLockProbeMetricsService;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProbeLockProbeMetricsHandler {

    private static final Logger log = LoggerFactory.getLogger(ProbeLockProbeMetricsHandler.class);

    private final ProbeLockProbeMetricsService probeService;

    public ProbeLockProbeMetricsHandler(ProbeLockProbeMetricsService probeService) {
        this.probeService = probeService;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJob message) {
        log.info("Received probe command for job ID: {}, device: {}, protocol: {}",
                message.getId(), message.getDeviceId(), message.getProtocol());

        boolean processed = probeService.processProbeWithLock(message);
        if (!processed) {
            log.warn("Probe execution postponed or skipped for job ID: {}", message.getId());
        }
    }
}
