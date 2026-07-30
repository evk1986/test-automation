package com.internal.netatlas.probe.handler;

import com.internal.netatlas.probe.model.ProbeJobMessage;
import com.internal.netatlas.probe.service.NetconfSubtreeService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

@Service
public class ProbeHandlersNetconfJobDispatcher {

    private final NetconfSubtreeService subtreeService;
    private final Counter netconfDispatchCounter;

    @Autowired
    public ProbeHandlersNetconfJobDispatcher(NetconfSubtreeService subtreeService,
                                              MeterRegistry meterRegistry) {
        this.subtreeService = subtreeService;
        this.netconfDispatchCounter = Counter.builder("netconf.subtree.dispatch")
                .description("Number of NETCONF subtree jobs dispatched")
                .register(meterRegistry);
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            return; // ignore non-NETCONF messages
        }
        netconfDispatchCounter.increment();
        subtreeService.processSubtreeJob(message);
    }
}
