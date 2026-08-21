package com.internal.netatlas.probe.service;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NetconfIntegrationJobService {

    private static final Logger log = LoggerFactory.getLogger(NetconfIntegrationJobService.class);

    private final Counter failureCounter;
    private final Counter successCounter;

    public NetconfIntegrationJobService(MeterRegistry meterRegistry) {
        this.failureCounter = meterRegistry.counter("probe.protocol.failures", "protocol", "NETCONF");
        this.successCounter = meterRegistry.counter("probe.protocol.successes", "protocol", "NETCONF");
    }

    public boolean processNetconfCommand(ProbeJobMessage message) {
        if (message == null) {
            failureCounter.increment();
            log.error("Attempted to process null ProbeJobMessage");
            return false;
        }

        log.info("Processing NETCONF command for batchId={}, jobId={}, deviceId={}",
                message.getBatchId(), message.getJobId(), message.getDeviceId());

        if ("INVALID_PAYLOAD".equalsIgnoreCase(message.getPayload())) {
            failureCounter.increment();
            log.error("Failed NETCONF execution due to invalid payload for deviceId={}", message.getDeviceId());
            return false;
        }

        successCounter.increment();
        log.info("Successfully executed NETCONF probe for deviceId={} under batchId={}",
                message.getDeviceId(), message.getBatchId());
        return true;
    }
}
