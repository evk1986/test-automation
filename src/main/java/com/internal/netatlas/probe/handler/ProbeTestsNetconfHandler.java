package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.ProbeTestsNetconfService;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
public class ProbeTestsNetconfHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProbeTestsNetconfHandler.class);

    private final ProbeTestsNetconfService netconfService;
    private final MeterRegistry meterRegistry;
    private final String probeCommandsQueue;

    public ProbeTestsNetconfHandler(ProbeTestsNetconfService netconfService,
                                    MeterRegistry meterRegistry,
                                    @Value("${aws.sqs.probe.commands}") String probeCommandsQueue) {
        this.netconfService = netconfService;
        this.meterRegistry = meterRegistry;
        this.probeCommandsQueue = probeCommandsQueue;
    }

    @SqsListener("${aws.sqs.probe.commands}")
    public void handle(ProbeJobMessage message) {
        LOG.info("Received ProbeJobMessage id={}, deviceId={}, protocol={}",
                message.getJobId(), message.getDeviceId(), message.getProtocol());
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            LOG.warn("Skipping non‑NETCONF job {}", message.getJobId());
            return;
        }
        try {
            netconfService.processNetconfJob(message);
            meterRegistry.counter("probe.netconf.success").increment();
        } catch (Exception e) {
            LOG.error("NETCONF processing failed for job {}: {}", message.getJobId(), e.getMessage(), e);
            meterRegistry.counter("probe.netconf.failure").increment();
            // In a real pipeline the message would be sent to DLQ; omitted for brevity
        }
    }
}
