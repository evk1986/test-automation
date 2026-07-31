package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.DocsRunbooksDocsAdrService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class DocsRunbooksDocsAdrHandler {

    private final DocsRunbooksDocsAdrService service;
    private final MeterRegistry meterRegistry;

    public DocsRunbooksDocsAdrHandler(DocsRunbooksDocsAdrService service, MeterRegistry meterRegistry) {
        this.service = service;
        this.meterRegistry = meterRegistry;
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (message == null || message.getProtocol() == null) {
            return;
        }
        // Process only NETCONF jobs – other protocols are ignored by this handler
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            return;
        }
        try {
            service.processNetconfJob(message);
            Counter success = meterRegistry.counter("netconf.handler.success");
            success.increment();
        } catch (Exception e) {
            Counter failure = meterRegistry.counter("netconf.handler.failure");
            failure.increment();
            // Propagate to let the SQS listener apply its DLQ policy
            throw e;
        }
    }
}
