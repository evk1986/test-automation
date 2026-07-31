package com.internal.netatlas.probe.handler.netconf;

import com.amazonaws.services.sqs.annotation.SqsListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ProbeHandlersNetconfSubtreeHandler {

    private final ProbeHandlersNetconfSubtreeService service;
    private final HazelcastInstance hazelcastInstance;
    private final Counter netconfFailureCounter;

    public ProbeHandlersNetconfSubtreeHandler(ProbeHandlersNetconfSubtreeService service,
                                              HazelcastInstance hazelcastInstance,
                                              MeterRegistry meterRegistry) {
        this.service = service;
        this.hazelcastInstance = hazelcastInstance;
        this.netconfFailureCounter = Counter.builder("probe.protocol.failures")
                .description("NETCONF protocol failures")
                .tag("protocol", "NETCONF")
                .register(meterRegistry);
    }

    @SqsListener("probe.commands")
    public void handle(ProbeJobMessage message) {
        // Only handle NETCONF jobs for Cisco IOS‑XR NCS devices
        if (!"NETCONF".equalsIgnoreCase(message.getProtocol())) {
            return;
        }
        if (!"IOS-XR".equalsIgnoreCase(message.getDeviceFamily())) {
            return;
        }

        String lockKey = "netconf-lock-" + message.getBatchId() + "-" + message.getDeviceId();
        ILock lock = hazelcastInstance.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(5, TimeUnit.SECONDS);
            if (!acquired) {
                netconfFailureCounter.increment();
                return;
            }
            service.process(message);
        } catch (Exception e) {
            netconfFailureCounter.increment();
            // In production we would forward the message to a DLQ; omitted here.
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
    }
}
