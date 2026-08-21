package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.service.ProbeLockProbeMetricsService;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProbeLockProbeMetricsHandler {

    private static final Logger log = LoggerFactory.getLogger(ProbeLockProbeMetricsHandler.class);
    private final ProbeLockProbeMetricsService service;

    public ProbeLockProbeMetricsHandler(ProbeLockProbeMetricsService service) {
        this.service = service;
    }

    @SqsListener("probe.commands")
    public void handle(String messagePayload) {
        String batchId = "BATCH-PRB-20240523-USE1-01";
        String deviceId = "DEV-ASR-9001";
        String protocol = "SNMP";
        String region = "prod-use1";

        boolean executed = service.executeWithLock(batchId, deviceId, protocol, region, () -> {
            log.info("Performing network probe operation for device {}", deviceId);
        });

        if (!executed) {
            log.warn("Execution skipped or failed for device {}", deviceId);
        }
    }
}
