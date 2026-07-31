package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.model.Protocol;
import com.internal.netatlas.probe.service.TestIntegrationNetconfService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class TestIntegrationNetconfHandler {

    private final TestIntegrationNetconfService service;
    private final MeterRegistry meterRegistry;

    public TestIntegrationNetconfHandler(TestIntegrationNetconfService service, MeterRegistry meterRegistry) {
        this.service = service;
        this.meterRegistry = meterRegistry;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Accept only NETCONF protocol messages; silently ignore others
        if (message == null || message.getProtocol() != Protocol.NETCONF) {
            return;
        }
        try {
            service.processProbeJob(message);
            Counter success = meterRegistry.counter("probe.protocol.success", "protocol", "NETCONF");
            success.increment();
        } catch (Exception e) {
            Counter failure = meterRegistry.counter("probe.protocol.failures", "protocol", "NETCONF");
            failure.increment();
            // Propagate exception so SQS retry / DLQ handling can take over
            throw e;
        }
    }
}
